/*
 * Copyright (c) 2021 Freya Arbjerg and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lavalink.server.player;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import lavalink.server.util.ByteRingBuffer;
import java.util.concurrent.TimeUnit;

public class AudioLossCounter extends AudioEventAdapter {
    public static final int EXPECTED_PACKET_COUNT_PER_MIN = (60 * 1000) / 20;
    
    private static final long ACCEPTABLE_TRACK_SWITCH_TIME = TimeUnit.MILLISECONDS.toNanos(100);
    private static final long ONE_SECOND = TimeUnit.SECONDS.toNanos(1);
    
    private final ByteRingBuffer nulled = new ByteRingBuffer(60);
    private final ByteRingBuffer sent = new ByteRingBuffer(60);
    private long playingSince = Long.MAX_VALUE;
    private long trackStart;
    private long lastTrackEnd;
    private long lastUpdate;
    private byte currentNulled;
    private byte currentSent;
    
    public void onSuccess() {
        checkTime();
        currentSent++;
    }
    
    public void onLoss() {
        checkTime();
        currentNulled++;
    }
    
    public ByteRingBuffer getLastMinuteNulled() {
        return nulled;
    }
    
    public ByteRingBuffer getLastMinuteSent() {
        return sent;
    }
    
    public boolean isDataUsable() {
        if(trackStart - lastTrackEnd > ACCEPTABLE_TRACK_SWITCH_TIME && lastTrackEnd != 0) {
            return false;
        }
        return TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - playingSince) >= 60;
    }
    
    private void checkTime() {
        long now = System.nanoTime();
        if(now - lastUpdate > ONE_SECOND) {
            lastUpdate = now;
            nulled.put(currentNulled);
            sent.put(currentSent);
            currentNulled = 0;
            currentSent = 0;
        }
    }
    
    private void start() {
        trackStart = System.nanoTime();
        if(trackStart - playingSince > ACCEPTABLE_TRACK_SWITCH_TIME || playingSince == Long.MAX_VALUE) {
            playingSince = trackStart;
            nulled.clear();
            sent.clear();
        }
    }

    private void end() {
        lastTrackEnd = System.nanoTime();
    }

    @Override
    public void onTrackEnd(AudioPlayer __, AudioTrack ___, AudioTrackEndReason ____) {
        end();
    }

    @Override
    public void onTrackStart(AudioPlayer __, AudioTrack ___) {
        start();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        end();
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        start();
    }
}
