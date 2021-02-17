package main;

import exception.SoundEffectException;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * The SoundEffect class allows a simple and easy interface to use when
 * loading, playing, or looping sound effects.
 *
 * @author Harry Felton
 */
public class SoundEffect {
    /**
     * Format of the sound effect
     */
    private final AudioFormat format;

    /**
     * The audio data that makes up the sound effect
     */
    private final byte[] data;

    /**
     * The length of the data buffer for this sound effect
     *
     * @see #data
     */
    private final long bufferLength;

    /**
     * The volume that should be used when playing this sound effect; can be set manually,
     * or overridden when giving the play command
     *
     * @see #setDecibelVolume(float)
     * @see #playLoop(float)
     * @see #playOnce(float)
     */
    private float decibelVolume = 1.0f;

    /**
     * Loop Clip - usually null unless the clip
     * is played in a looped fashion, in which case
     * this is populated with a Clip (from AudioSystem.getClip) which
     * is loaded with this sound effect and told to loop.
     */
    private Clip mLoopClip;

    /**
     * Gets the loop clip to use, if any exists
     *
     * @return The Clip to loop, or null if none generated yet
     */
    public Clip getLoopClip() {
        return mLoopClip;
    }

    /**
     * Sets the clip to use when looping this sound effect
     *
     * @param clip The AudioSystem Clip to loop
     */
    public void setLoopClip(Clip clip) {
        mLoopClip = clip;
    }

    /**
     * Returns the volume (in decibels) that this audio clip will be played at unless
     * a different volume is specified when giving the play command
     *
     * @return The default decibel volume
     * @see #playOnce(float)
     * @see #playLoop(float)
     */
    public float getDecibelVolume() {
        return decibelVolume;
    }

    /**
     * Sets the volume used when playing this audio clip, unless a different value is given when
     * playing the sound clip.
     *
     * @param decibelVolume The volume (decibels) to play this clip at unless specified otherwise
     * @see #playLoop(float)
     * @see #playOnce(float)
     */
    public void setDecibelVolume(float decibelVolume) {
        this.decibelVolume = decibelVolume;
    }

    /**
     * Get the audio format this sound effect is specified in
     *
     * @return The audio format
     */
    public AudioFormat getAudioFormat() {
        return format;
    }

    /**
     * Fetch the byte array of data that specifies this audio
     *
     * @return Data array
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Get the length of the data buffer currently occupying the {@code data} field.
     *
     * @return The length of the data array
     */
    public long getBufferSize() {
        return bufferLength;
    }

    /**
     * Constructs a new sound effect based on the provided input stream
     *
     * @param stream The stream to use
     */
    protected SoundEffect(AudioInputStream stream) {
        format = stream.getFormat();
        bufferLength = stream.getFrameLength() * format.getFrameSize();
        data = new byte[(int)bufferLength];

        try {
            stream.read(data);
        } catch(Exception e) {
            System.out.println("Error reading Audio File: \n" + e.getMessage());
            System.exit(1);
        }

        mLoopClip = null;
    }

    /**
     * A private method used to simplify the play(Once/Loop) methods. This method will create an
     * AudioSystem clip, based on the stream currently loaded and the volume provided to this method.
     *
     * @param volume The volume (decibels) to play this clip at
     * @return The clip to be played
     * @throws SoundEffectException Thrown if an exception occurs when creating the Clip.
     */
    private Clip generateClip(float volume) throws SoundEffectException {
        try {
            // Load clip
            Clip clip = AudioSystem.getClip();
            clip.open(format, data, 0, (int) bufferLength);

            // Adjust volume
            FloatControl control = (FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(volume);

            return clip;
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new SoundEffectException(e.getMessage());
        }
    }

    /**
     * Plays this sound effect once using the volume provided
     *
     * @param volume The volume (decibels) to play at
     */
    public void playOnce(float volume) {
        Clip c;
        try {
            c = generateClip(volume);
            c.start();
        } catch (SoundEffectException e) {
            System.err.println("FAILED to play SoundEffect! Clip generation failure: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Plays this sound effect once with the default volume specified by {@code decibelVolume}
     *
     * @see #decibelVolume
     */
    public void playOnce() {
        playOnce(decibelVolume);
    }

    /**
     * Loops the sound effect at the volume provided
     *
     * @param volume The decibel volume to play at
     * @throws SoundEffectException Thrown when a failure occurs during the creation of the audio clip
     */
    public void playLoop(float volume) throws SoundEffectException {
        Clip c = getLoopClip();
        if(c == null) {
            try {
                c = generateClip(volume);
                c.loop(Clip.LOOP_CONTINUOUSLY);

                mLoopClip = c;
            } catch (SoundEffectException e) {
                System.err.println("FAILED to loop SoundEffect! Clip generation failure: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if(c == null) {
            throw new SoundEffectException("Failed to loop SoundEffect, unknown failure caused clip to be unavailable!");
        }

        c.setFramePosition(0);
        c.start();
    }

    /**
     * Loops the sound effect at the default volume specified by {@code decibelVolume}
     *
     * @throws SoundEffectException Thrown if the {@code playLoop(float)} method throws this exception.
     * @see #decibelVolume
     * @see #playOnce(float)
     */
    public void playLoop() throws SoundEffectException {
        playLoop(decibelVolume);
    }

    /**
     * Stops looping this audio if it's currently looping.
     *
     * @see #mLoopClip
     */
    public void stopLoop() {
        if(mLoopClip != null) {
            mLoopClip.stop();
        }
    }

    /**
     * Loads the sound effect by opening an audio stream at the path provided, and loading the audio
     * data in to a {@code SoundEffect} instance.
     *
     * @param path The file to load the audio from
     * @return The SoundEffect instance used to play/loop this audio
     */
    public static SoundEffect loadSoundEffect(String path) {
        try {
            File file = new File(path);
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);

            return new SoundEffect(inputStream);
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio clip found when trying to load input stream, stack follows: " + Arrays.toString(e.getStackTrace()));
        } catch (IOException e) {
            System.err.println("Unknown IOException occurred when loading sound effect; please check the filepath provided. Stack follows: " + Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            // Not an error we were prepared for, let the caller handle this
            System.err.println("Unknown fatal exception. Propagating from loading sound effect (SoundEffect.loadSoundEffect)");
            throw e;
        }

        return null;
    }
}
