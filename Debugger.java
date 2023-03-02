import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.cache.*;

import java.text.NumberFormat;
import java.lang.StringBuilder;

import java.lang.management.*;
import java.lang.management.ManagementFactory;
import com.sun.management.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


import java.io.File;

public class Debugger extends ListenerAdapter {

  SlashCommandInteractionEvent slashEvent;
  private Runtime runtime = Runtime.getRuntime();
  
  public void onSlashCommandInteraction(SlashCommandInteractionEvent event){
        slashEvent = event;
        if (event.getName().equals("debug")){
          event.deferReply(true).queue();

          if (event.getSubcommandName().equals("performance")) performanceHandler();

          if (event.getSubcommandName().equals("cache"))
          {
            if (event.getOption("message-id") == null){
            cacheHandler();
            }
            else {
              cacheHandler(event.getOption("message-id").getAsString());
            }
          }
        }
      
  }




  private void performanceHandler(){
    slashEvent.getHook().editOriginal(" ").setEmbeds(getPerformanceEmbed()).queue();
  }

  private void cacheHandler(){
    slashEvent.getHook().editOriginal("Currently storing " + MessageCache.getSize() + " messages in cache.").queue();
  }

  private void cacheHandler(String messageId){
    slashEvent.getHook().editOriginal(MessageCache.getMessageById(String.valueOf(messageId)).getContentRaw()).queue();
  }

















  


  private MessageEmbed getPerformanceEmbed(){
    MessageEmbed info = new EmbedBuilder()
      .setTitle("Performance Info")
      .addField("Disk Info", diskInfo(), true)
      .addField("Mem Info", memInfo(), true)
      .addField("OS Info", osInfo(), true)
      .addField("CPU Usage", String.valueOf(getCPUUsage()) + "%", true)
      .build();
    return info;
  }



  private String osArch() {
        return System.getProperty("os.arch");
    }
   private String osName() {
        return System.getProperty("os.name");
    }

   private long usedMem() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

  private long totalMem() {
        return Runtime.getRuntime().totalMemory();
    }

  private String osVersion() {
        return System.getProperty("os.version");
    }

  private String diskInfo() {
        /* Get a list of all filesystem roots on this system */
        File[] roots = File.listRoots();
        StringBuilder sb = new StringBuilder();

        /* For each filesystem root, print some info */
        for (File root : roots) {
            sb.append("**File system root: **\n");
            sb.append(root.getAbsolutePath());
            sb.append("\n");
            sb.append("**Total space (bytes): **\n");
            sb.append(root.getTotalSpace());
            sb.append("\n");
            sb.append("**Free space (bytes): **\n");
            sb.append(root.getFreeSpace());
            sb.append("\n");
            sb.append("**Usable space (bytes): **\n");
            sb.append(root.getUsableSpace());
            
        }
        return sb.toString();
    }

  public String memInfo() {
        NumberFormat format = NumberFormat.getInstance();
        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        sb.append("Free memory: ");
        sb.append(format.format(freeMemory / 1024));
        sb.append("\n");
        sb.append("Allocated memory: ");
        sb.append(format.format(allocatedMemory / 1024));
        sb.append("\n");
        sb.append("Max memory: ");
        sb.append(format.format(maxMemory / 1024));
        sb.append("\n");
        sb.append("Total free memory: ");
        sb.append(format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024));
        sb.append("\n");
        return sb.toString();

    }

  public String osInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("OS: ");
        sb.append(this.osName());
        sb.append("<br/>");
        sb.append("Version: ");
        sb.append(this.osVersion());
        sb.append("<br/>");
        sb.append(": ");
        sb.append(this.osArch());
        sb.append("<br/>");
        sb.append("Available processors (cores): ");
        sb.append(runtime.availableProcessors());
        sb.append("<br/>");
        return sb.toString();
    }

  private Double getCPUUsage(){
    com.sun.management.OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
    int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
    long prevUpTime = runtimeMXBean.getUptime();
    long prevProcessCpuTime = operatingSystemMXBean.getProcessCpuTime();
    double cpuUsage;
    try
    {
        Thread.sleep(500);
    }
    catch (Exception ignored) { }

    operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    long upTime = runtimeMXBean.getUptime();
    long processCpuTime = operatingSystemMXBean.getProcessCpuTime();
    long elapsedCpu = processCpuTime - prevProcessCpuTime;
    long elapsedTime = upTime - prevUpTime;

    cpuUsage = Math.min(99F, elapsedCpu / (elapsedTime * 10000F * availableProcessors));
    return cpuUsage;
  }
}