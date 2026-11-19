package com.example.demo.filesystem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Assumptions;

class FileSystemToolsTests {
  @TempDir Path temporary;

  @Test
  void managesFilesWithinRoot() throws IOException {
    var tools = new FileSystemTools(temporary.resolve("root"));
    tools.createDirectory("notes");
    tools.writeFile("notes/a.txt", "안녕하세요", false);
    assertEquals("안녕하세요", tools.readFile("notes/a.txt"));
    assertThrows(IOException.class, () -> tools.writeFile("notes/a.txt", "changed", false));
    tools.writeFile("notes/a.txt", "수정", true);
    tools.copyFile("notes/a.txt", "notes/b.txt");
    tools.moveFile("notes/b.txt", "notes/c.txt");
    assertEquals("수정", tools.readFile("notes/c.txt"));
    assertEquals(2, tools.listFiles("notes").size());
    assertThrows(IOException.class, () -> tools.deleteFile("notes"));
    tools.deleteFile("notes/a.txt");
    tools.deleteFile("notes/c.txt");
    tools.deleteFile("notes");
    assertTrue(tools.listFiles(".").isEmpty());
  }

  @Test
  void rejectsEscapesAndRootMutation() throws IOException {
    var tools = new FileSystemTools(temporary.resolve("root"));
    assertThrows(IllegalArgumentException.class, () -> tools.readFile("../outside.txt"));
    assertThrows(IllegalArgumentException.class, () -> tools.readFile(temporary.resolve("outside.txt").toString()));
    assertThrows(IllegalArgumentException.class, () -> tools.writeFile("a.txt:stream", "text", false));
    assertThrows(IllegalArgumentException.class, () -> tools.deleteFile("."));
    assertThrows(IllegalArgumentException.class, () -> tools.writeFile("notes/..", "text", true));
    tools.writeFile("a.txt", "original", false);
    assertThrows(IllegalArgumentException.class, () -> tools.copyFile("a.txt", "../copy.txt"));
    assertThrows(IllegalArgumentException.class, () -> tools.moveFile("a.txt", "../moved.txt"));
    assertEquals("original", tools.readFile("a.txt"));
  }

  @Test
  void rejectsLinksToOutsideRoot() throws IOException {
    Path root = Files.createDirectory(temporary.resolve("root"));
    Path outside = Files.createDirectory(temporary.resolve("outside"));
    Files.writeString(outside.resolve("secret.txt"), "secret");
    try {
      Files.createSymbolicLink(root.resolve("link"), outside);
    } catch (IOException | UnsupportedOperationException | SecurityException exception) {
      Assumptions.assumeTrue(false, "Symbolic links are unavailable: " + exception.getMessage());
    }
    var tools = new FileSystemTools(root);
    assertThrows(IOException.class, () -> tools.readFile("link/secret.txt"));
    assertThrows(IOException.class, () -> tools.writeFile("link/new.txt", "text", false));
    assertThrows(IOException.class, () -> tools.deleteFile("link/secret.txt"));
    assertEquals("secret", Files.readString(outside.resolve("secret.txt")));
  }
}
