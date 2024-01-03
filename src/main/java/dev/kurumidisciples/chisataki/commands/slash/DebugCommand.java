package dev.kurumidisciples.chisataki.commands.slash;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.NumberFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import dev.kurumidisciples.chisataki.utils.MessageCache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class DebugCommand extends SlashCommand {
	private Runtime runtime = Runtime.getRuntime();

	public DebugCommand() {
		super("debug", "brings up debugging info for the bot", Permission.VIEW_AUDIT_LOGS);
		this.subcommands = List.of(
				new SubcommandData("performance", "brings up performance stats"),
				new SubcommandData("cache", "view the system cache").addOption(OptionType.STRING, "message-id", "grab message id")
				);
	}

	@Override
	public void execute(SlashCommandInteractionEvent event) {
		event.deferReply(true).queue();

		if (event.getSubcommandName().equals("performance")) {
			performanceHandler(event);
		} else if (event.getSubcommandName().equals("cache")) {
			cacheHandler(event);
		}
	}
	
	@Override
	public boolean isAllowed(SlashCommandInteractionEvent event) {
		return true;
	}
	
	@Override
	public String getErrorMessage() {
		throw new UnsupportedOperationException();
	}

	private void performanceHandler(SlashCommandInteractionEvent event) {
		event.getHook().editOriginal("").setEmbeds(getPerformanceEmbed()).queue();
	}

	private void cacheHandler(SlashCommandInteractionEvent event) {
		OptionMapping messageOption = event.getOption("message-id");
		
		String message;
		if (messageOption == null) {
			message =  "Currently storing " + MessageCache.getSize() + " messages in cache.";
		} else {
			String messageId = messageOption.getAsString();
			Message messageCache = MessageCache.getMessageById(messageId);
			String messageText = messageCache != null ? messageCache.getContentRaw() : null;
			message = StringUtils.isBlank(messageText) ? "> Message with given id could not be retrieved." : messageText;
		}

		event.getHook().editOriginal(message).queue();
	}

	private MessageEmbed getPerformanceEmbed(){
		return new EmbedBuilder()
				.setTitle("Performance Info")
				.addField("Disk Info", diskInfo(), true)
				.addField("Mem Info", memInfo(), true)
				.addField("OS Info", osInfo(), true)
				.addField("CPU Usage", String.valueOf(getCPUUsage()) + "%", true)
				.build();
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
		sb.append("\n");
		sb.append("Version: ");
		sb.append(this.osVersion());
		sb.append("\n");
		sb.append("Arch: ");
		sb.append(this.osArch());
		sb.append("\n");
		sb.append("Available processors (cores): ");
		sb.append(runtime.availableProcessors());
		sb.append("\n");
		return sb.toString();
	}

	private String osName() {
		return System.getProperty("os.name");
	}
	
	private String osVersion() {
		return System.getProperty("os.version");
	}

	private String osArch() {
		return System.getProperty("os.arch");
	}

	private Double getCPUUsage(){
		com.sun.management.OperatingSystemMXBean operatingSystemMXBean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
		int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
		long prevUpTime = runtimeMXBean.getUptime();
		long prevProcessCpuTime = operatingSystemMXBean.getProcessCpuTime();
		double cpuUsage;
		try {
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
