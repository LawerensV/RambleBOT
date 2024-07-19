package net.ramblemc.bot;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.EnumSet;

@Getter
public final class RambleBOT extends JavaPlugin {

    private static JDA jda;
    private static final String JDA_VERSION = "5.0.1";
    private static final String JDA_URL = "https://repo1.maven.org/maven2/net/dv8tion/JDA/" + JDA_VERSION + "/JDA-" + JDA_VERSION + ".jar";
    private static final String JDA_FILE_NAME = "JDA-" + JDA_VERSION + ".jar";

    @Override
    public void onEnable() {
        try {
            File pluginFolder = getDataFolder();
            if (!pluginFolder.exists()) {
                pluginFolder.mkdirs();
            }

            File jdaFile = new File(pluginFolder, JDA_FILE_NAME);
            if (!jdaFile.exists()) {
                getLogger().info("Downloading JDA...");
                downloadFile(jdaFile);
            }

            URLClassLoader urlClassLoader = (URLClassLoader) getClassLoader();
            URL url = jdaFile.toURI().toURL();
            addURL(urlClassLoader, url);

            getLogger().info("JDA loaded successfully.");

            saveDefaultConfig();
            jda = JDABuilder.createLight(
                    getConfig().getString("token"),
                    EnumSet.of(
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS
                    )
            ).build();
        } catch (Exception e) {
            getLogger().severe("Failed to load JDA: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void downloadFile(File destination) throws Exception {
        URL url = new URL(RambleBOT.JDA_URL);
        try (ReadableByteChannel rbc = Channels.newChannel(url.openStream());
             FileOutputStream fos = new FileOutputStream(destination)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    private void addURL(URLClassLoader classLoader, URL url) throws Exception {
        java.lang.reflect.Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(classLoader, url);
    }

    @Override
    public void onDisable() {

    }
}
