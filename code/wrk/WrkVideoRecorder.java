/*
 * Worker pour l'enregistrement vidéo des sessions robot
 * Utilise JavaCV (wrapper Java pour OpenCV/FFmpeg) pour encoder les frames en MP4
 * 
 * @author SpeleoThink Team
 */
package wrk;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Worker pour l'enregistrement vidéo des flux du robot.
 * Encode les frames JPEG reçues en vidéo MP4 avec codec H.264.
 */
public class WrkVideoRecorder {

    // Configuration vidéo
    private static final int VIDEO_WIDTH = 640;
    private static final int VIDEO_HEIGHT = 480;
    private static final int FRAME_RATE = 10; // ~10 FPS (adapté au débit du robot)
    private static final int VIDEO_BITRATE = 2000000; // 2 Mbps
    
    // Dossier de sauvegarde des enregistrements
    private static final String RECORDINGS_FOLDER = "recordings";
    
    // État de l'enregistrement
    private volatile boolean isRecording = false;
    private FFmpegFrameRecorder recorder;
    private Java2DFrameConverter converter;
    private Thread recordingThread;
    private BlockingQueue<byte[]> frameQueue;
    private String currentFilePath;
    private long recordingStartTime;
    private long frameCount;
    
    // Listener pour notifier l'UI
    private VideoRecorderListener listener;

    /**
     * Interface pour notifier l'UI des événements d'enregistrement
     */
    public interface VideoRecorderListener {
        void onRecordingStarted(String filePath);
        void onRecordingStopped(String filePath, long durationMs, long frameCount);
        void onRecordingError(String error);
        void onFrameRecorded(long frameNumber);
    }

    public WrkVideoRecorder() {
        this.frameQueue = new LinkedBlockingQueue<>(100); // Buffer de 100 frames max
        this.converter = new Java2DFrameConverter();
        
        // Créer le dossier d'enregistrements s'il n'existe pas
        File recordingsDir = new File(RECORDINGS_FOLDER);
        if (!recordingsDir.exists()) {
            recordingsDir.mkdirs();
        }
    }

    public void setListener(VideoRecorderListener listener) {
        this.listener = listener;
    }

    /**
     * Démarre l'enregistrement vidéo.
     * @return true si l'enregistrement a démarré avec succès
     */
    public synchronized boolean startRecording() {
        if (isRecording) {
            System.out.println("[VIDEO] Enregistrement déjà en cours");
            return false;
        }

        try {
            // Générer le nom de fichier avec timestamp
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            currentFilePath = RECORDINGS_FOLDER + File.separator + "mission_" + timestamp + ".mp4";
            
            // Initialiser le recorder FFmpeg
            recorder = new FFmpegFrameRecorder(currentFilePath, VIDEO_WIDTH, VIDEO_HEIGHT);
            recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
            recorder.setFormat("mp4");
            recorder.setFrameRate(FRAME_RATE);
            recorder.setVideoBitrate(VIDEO_BITRATE);
            recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
            
            // Options de qualité pour H.264
            recorder.setVideoOption("preset", "ultrafast"); // Encodage rapide
            recorder.setVideoOption("tune", "zerolatency"); // Latence minimale
            recorder.setVideoOption("crf", "23"); // Qualité (0-51, 23 = défaut)
            
            recorder.start();
            
            isRecording = true;
            recordingStartTime = System.currentTimeMillis();
            frameCount = 0;
            frameQueue.clear();
            
            // Démarrer le thread d'encodage
            recordingThread = new Thread(this::encodingLoop, "VideoEncodingThread");
            recordingThread.start();
            
            System.out.println("[VIDEO] Enregistrement démarré: " + currentFilePath);
            
            if (listener != null) {
                listener.onRecordingStarted(currentFilePath);
            }
            
            return true;
            
        } catch (Exception e) {
            System.err.println("[VIDEO] Erreur démarrage enregistrement: " + e.getMessage());
            e.printStackTrace();
            isRecording = false;
            
            if (listener != null) {
                listener.onRecordingError("Erreur démarrage: " + e.getMessage());
            }
            
            return false;
        }
    }

    /**
     * Arrête l'enregistrement vidéo.
     * @return le chemin du fichier enregistré, ou null en cas d'erreur
     */
    public synchronized String stopRecording() {
        if (!isRecording) {
            System.out.println("[VIDEO] Aucun enregistrement en cours");
            return null;
        }

        isRecording = false;
        
        try {
            // Attendre que le thread d'encodage se termine
            if (recordingThread != null) {
                recordingThread.interrupt();
                recordingThread.join(5000); // Attendre max 5 secondes
            }
            
            // Fermer le recorder
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
            
            long duration = System.currentTimeMillis() - recordingStartTime;
            
            System.out.println("[VIDEO] Enregistrement terminé: " + currentFilePath);
            System.out.println("[VIDEO] Durée: " + (duration / 1000) + "s, Frames: " + frameCount);
            
            if (listener != null) {
                listener.onRecordingStopped(currentFilePath, duration, frameCount);
            }
            
            return currentFilePath;
            
        } catch (Exception e) {
            System.err.println("[VIDEO] Erreur arrêt enregistrement: " + e.getMessage());
            e.printStackTrace();
            
            if (listener != null) {
                listener.onRecordingError("Erreur arrêt: " + e.getMessage());
            }
            
            return null;
        }
    }

    /**
     * Ajoute une frame à la queue d'enregistrement.
     * Appelé depuis le thread principal quand une image est reçue.
     * 
     * @param jpegData les données JPEG de l'image
     */
    public void addFrame(byte[] jpegData) {
        if (!isRecording || jpegData == null || jpegData.length == 0) {
            return;
        }
        
        // Ajouter à la queue (non-bloquant, drop si pleine)
        if (!frameQueue.offer(jpegData)) {
            System.out.println("[VIDEO] Queue pleine, frame ignorée");
        }
    }

    /**
     * Boucle d'encodage exécutée dans un thread séparé.
     */
    private void encodingLoop() {
        System.out.println("[VIDEO] Thread d'encodage démarré");
        
        while (isRecording || !frameQueue.isEmpty()) {
            try {
                // Récupérer la prochaine frame (avec timeout)
                byte[] jpegData = frameQueue.poll(100, TimeUnit.MILLISECONDS);
                
                if (jpegData != null) {
                    encodeFrame(jpegData);
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[VIDEO] Erreur encodage frame: " + e.getMessage());
            }
        }
        
        System.out.println("[VIDEO] Thread d'encodage terminé");
    }

    /**
     * Encode une frame JPEG en vidéo.
     */
    private void encodeFrame(byte[] jpegData) {
        try {
            // Convertir JPEG en BufferedImage
            ByteArrayInputStream bais = new ByteArrayInputStream(jpegData);
            BufferedImage image = ImageIO.read(bais);
            
            if (image == null) {
                System.err.println("[VIDEO] Impossible de décoder l'image JPEG");
                return;
            }
            
            // Redimensionner si nécessaire
            if (image.getWidth() != VIDEO_WIDTH || image.getHeight() != VIDEO_HEIGHT) {
                image = resizeImage(image, VIDEO_WIDTH, VIDEO_HEIGHT);
            }
            
            // Convertir en Frame pour FFmpeg
            Frame frame = converter.convert(image);
            
            // Enregistrer la frame
            if (recorder != null && frame != null) {
                recorder.record(frame);
                frameCount++;
                
                // Notifier tous les 10 frames
                if (frameCount % 10 == 0 && listener != null) {
                    listener.onFrameRecorded(frameCount);
                }
            }
            
        } catch (IOException e) {
            System.err.println("[VIDEO] Erreur lecture image: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[VIDEO] Erreur encodage: " + e.getMessage());
        }
    }

    /**
     * Redimensionne une image à la taille cible.
     */
    private BufferedImage resizeImage(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        java.awt.Graphics2D g = resized.createGraphics();
        g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, 
                          java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    /**
     * @return true si un enregistrement est en cours
     */
    public boolean isRecording() {
        return isRecording;
    }

    /**
     * @return le chemin du fichier en cours d'enregistrement
     */
    public String getCurrentFilePath() {
        return currentFilePath;
    }

    /**
     * @return la durée d'enregistrement en secondes
     */
    public long getRecordingDurationSeconds() {
        if (!isRecording) {
            return 0;
        }
        return (System.currentTimeMillis() - recordingStartTime) / 1000;
    }

    /**
     * @return le nombre de frames enregistrées
     */
    public long getFrameCount() {
        return frameCount;
    }

    /**
     * Libère les ressources.
     */
    public void dispose() {
        if (isRecording) {
            stopRecording();
        }
        if (converter != null) {
            converter = null;
        }
    }
}
