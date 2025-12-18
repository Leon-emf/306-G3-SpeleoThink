/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package api_robot7links_test.wrk;

import com.voicerss.tts.AudioCodec;
import com.voicerss.tts.Languages;
import com.voicerss.tts.VoiceParameters;
import com.voicerss.tts.VoiceProvider;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author AudergonV01
 */
public class WrkAudio   {

    private static final String VOICE_RSS_API_KEY = "420b9119feff4990b01f091b3a4dbdbc";
    
    public WrkAudio() {
    }

    public byte[] audioFileToBytes(File f) throws IOException, UnsupportedAudioFileException {
        byte[] datas = null;
        if (f.exists()) {
            AudioInputStream is = AudioSystem.getAudioInputStream(f);
            DataInputStream dis = new DataInputStream(is);
            try {
                AudioFormat format = is.getFormat();
                byte[] auddatas = new byte[(int) (is.getFrameLength() * format.getFrameSize())];
                dis.readFully(auddatas);
                datas = new byte[auddatas.length-44]; //44-> longueur de l'header wav RIFF
                System.arraycopy(auddatas, 44, datas, 0, datas.length);
            } finally {
                dis.close();
            }
        } else {
            System.err.println("Le fichier " + f.getAbsolutePath() + " n'existe pas.");
        }
        return datas;
    }

    public byte[] textToSpeech(String text) {
        byte[] audioData = null;
        try {
            VoiceProvider tts = new VoiceProvider(VOICE_RSS_API_KEY);
            VoiceParameters params = new VoiceParameters(text, Languages.French_France);
            params.setCodec(AudioCodec.WAV);
            params.setFormat("11khz_16bit_mono");
            params.setBase64(false);
            params.setSSML(false);
            params.setRate(0);
            byte[] voice = tts.speech(params);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            os.write(voice, 0, voice.length);
            os.flush();
            byte[] wav = os.toByteArray();
            os.close();
            audioData = new byte[wav.length - 44];
            System.arraycopy(wav, 44, audioData, 0, audioData.length);
        } catch (Exception ex) {
            Logger.getLogger(WrkAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        return audioData;
    }

}
