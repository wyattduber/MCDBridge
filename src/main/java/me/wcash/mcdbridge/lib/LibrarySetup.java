package me.wcash.mcdbridge.lib;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.wcash.mcdbridge.MCDBridge;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class LibrarySetup implements AbstractLibraryLoader<Library> {

    private final BukkitLibraryManager bukkitLibraryManager = new BukkitLibraryManager(MCDBridge.getPlugin(MCDBridge.class));
    private final MCDBridge mcdb = MCDBridge.getPlugin();

    public List<Library> initLibraries() {

        List<Library> list = new java.util.ArrayList<>(Collections.emptyList());

        try {
            File jsonFile = getAzimFile();
            ObjectMapper objectMapper = new ObjectMapper();

            for (LibraryObject libraryObject : objectMapper.readValue(jsonFile, LibraryObject[].class)) {
                list.add(createLibrary(libraryObject));
                if (mcdb.debugMode) mcdb.debug("Loaded " + libraryObject.artifactId() + " " + libraryObject.version() + " from " + libraryObject.groupId());
            }
        } catch (IOException e) {
            mcdb.error(e.getMessage());
        }

        return list;
    }

    public void loadLibraries() {
        bukkitLibraryManager.addMavenCentral();
        bukkitLibraryManager.addMavenLocal();
        bukkitLibraryManager.addJCenter();
        bukkitLibraryManager.addJitPack();
        initLibraries().forEach(bukkitLibraryManager::loadLibrary);
    }

    private Library createLibrary(LibraryObject libraryObject) {
        return Library.builder().groupId(libraryObject.groupId()).artifactId(libraryObject.artifactId()).version(libraryObject.version()).relocate(libraryObject.oldRelocation(), libraryObject.newRelocation()).build();
    }

    private File getAzimFile() throws IOException {
        InputStream inputStream = mcdb.getResource("AzimDP.json");

        // Create a temporary file
        File tempFile = File.createTempFile("temp", ".tmp");

        // Write the content of the InputStream to the temporary file
        try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            assert inputStream != null;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (AssertionError e) {
            mcdb.error("Error creating temporary file! Stack Trace:");
            mcdb.error(e.getMessage());
        }

        return tempFile;

    }

}

record LibraryObject(String groupId, String artifactId, String version, String oldRelocation, String newRelocation) {
    public LibraryObject {
        if (groupId == null || artifactId == null || version == null) {
            throw new IllegalArgumentException("LibraryObject cannot have null groupId, artifactId, or version");
        }
    }
}
