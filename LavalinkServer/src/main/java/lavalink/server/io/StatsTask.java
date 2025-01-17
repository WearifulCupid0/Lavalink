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

package lavalink.server.io;

import lavalink.server.Launcher;
import lavalink.server.player.AudioLossCounter;
import lavalink.server.player.Player;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

public class StatsTask implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(StatsTask.class);

    private final SocketContext context;
    private final SocketServer socketServer;

    private final SystemInfo si = new SystemInfo();
    private final HardwareAbstractionLayer hal = si.getHardware();
    /** CPU ticks used for calculations in CPU load. */
    private long[] prevTicks;

    StatsTask(SocketContext context, SocketServer socketServer) {
        this.context = context;
        this.socketServer = socketServer;
    }

    @Override
    public void run() {
        try {
            sendStats();
        } catch (Exception e) {
            log.error("Exception while sending stats", e);
        }
    }

    private void sendStats() {
        if (context.getSessionPaused()) return;

        JSONObject out = new JSONObject();

        final int[] playersTotal = {0};
        final int[] playersPlaying = {0};

        socketServer.getContexts().forEach(socketContext -> {
            playersTotal[0] += socketContext.getPlayers().size();
            playersPlaying[0] += socketContext.getPlayingPlayers().size();
        });

        out.put("op", "stats");
        out.put("players", new JSONObject().put("playing", playersPlaying[0]).put("total", playersTotal[0]));
        out.put("uptime", System.currentTimeMillis() - Launcher.INSTANCE.getStartTime());

        // In bytes
        JSONObject mem = new JSONObject();
        mem.put("free", Runtime.getRuntime().freeMemory());
        mem.put("used", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        mem.put("allocated", Runtime.getRuntime().totalMemory());
        mem.put("reservable", Runtime.getRuntime().maxMemory());
        out.put("memory", mem);


        JSONObject cpu = new JSONObject();
        cpu.put("cores", Runtime.getRuntime().availableProcessors());
        // prevTicks will be null so set it to a value.
        if(prevTicks == null) {
            prevTicks = hal.getProcessor().getSystemCpuLoadTicks();
        }
        // Compare current CPU ticks with previous to establish a CPU load and return double.
        cpu.put("systemLoad", hal.getProcessor().getSystemCpuLoadBetweenTicks(prevTicks));
        // Set new prevTicks to current value for more accurate baseline, and checks in next schedule.
        prevTicks = hal.getProcessor().getSystemCpuLoadTicks();
        double load = getProcessRecentCpuUsage();
        if (!Double.isFinite(load)) load = 0;
        cpu.put("lavalinkLoad", load);

        out.put("cpu", cpu);

        int[] frameStats = new int[3];
        
        context.getPlayingPlayers().forEach(player -> {
            AudioLossCounter counter = player.getAudioLossCounter();
            if(counter.isDataUsable()) {
                frameStats[0]++;
                frameStats[1] += counter.getLastMinuteSent().sum();
                frameStats[2] += counter.getLastMinuteNulled().sum();
            }
        });
        
        int totalPlayers = frameStats[0];
        int totalSent = frameStats[1];
        int totalNulled = frameStats[2];
        
        int totalDeficit = totalPlayers * AudioLossCounter.EXPECTED_PACKET_COUNT_PER_MIN
                - (totalSent + totalNulled);
        
        // We can't divide by 0
        if(totalPlayers != 0) {
            out.put("frameStats", new JSONObject()
            .put("sent", totalSent / totalPlayers)
            .put("nulled", totalNulled / totalPlayers)
            .put("deficit", totalDeficit / totalPlayers));
        }

        context.send(out);
    }

    private double uptime = 0;
    private double cpuTime = 0;

    private double getProcessRecentCpuUsage() {
        double output;
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();
        OSProcess p = os.getProcess(os.getProcessId());

        if (cpuTime != 0) {
            double uptimeDiff = p.getUpTime() - uptime;
            double cpuDiff = (p.getKernelTime() + p.getUserTime()) - cpuTime;
            output = cpuDiff / uptimeDiff;
        } else {
            output = ((double) (p.getKernelTime() + p.getUserTime())) / (double) p.getUserTime();
        }

        // Record for next invocation
        uptime = p.getUpTime();
        cpuTime = p.getKernelTime() + p.getUserTime();
        return output / hal.getProcessor().getLogicalProcessorCount();
    }

}
