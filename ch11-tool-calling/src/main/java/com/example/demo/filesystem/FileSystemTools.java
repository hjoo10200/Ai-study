package com.example.demo.filesystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
public class FileSystemTools {
  private final Path root;

  public FileSystemTools() {
    this(Path.of(System.getProperty("user.home"), "document", "root"));
  }

  // 테스트에서는 임시 디렉토리를 루트로 사용한다.
  FileSystemTools(Path root) {
    this.root = root.toAbsolutePath().normalize();
  }

  @Tool(description = "관리 루트 내부의 디렉토리 목록을 조회합니다. 루트 조회에는 .을 사용하세요. 모든 도구는 루트 기준 상대 경로만 받습니다.")
  public List<Item> listFiles(@ToolParam(description = "루트 기준 디렉토리 상대 경로. 루트는 .") String path) throws IOException {
    Path directory = resolve(path);
    try (var entries = Files.list(directory)) {
      return entries.sorted().map(entry -> new Item(
          root.relativize(entry).toString(),
          Files.isDirectory(entry, LinkOption.NOFOLLOW_LINKS))).toList();
    }
  }

  @Tool(description = "관리 루트 내부의 UTF-8 텍스트 파일을 읽습니다.")
  public String readFile(@ToolParam(description = "파일 상대 경로") String path) throws IOException {
    return Files.readString(regularFile(path), StandardCharsets.UTF_8);
  }

  @Tool(description = "관리 루트 내부에 UTF-8 텍스트 파일을 저장합니다. 부모 디렉토리는 먼저 생성해야 합니다. 기존 파일 수정은 overwrite=true일 때만 가능합니다.")
  public String writeFile(@ToolParam(description = "파일 상대 경로") String path,
      @ToolParam(description = "저장할 전체 텍스트") String content,
      @ToolParam(description = "기존 파일을 덮어쓸지 여부") boolean overwrite) throws IOException {
    Path target = mutablePath(path);
    if (overwrite) {
      Files.writeString(target, content, StandardCharsets.UTF_8,
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
    } else {
      Files.writeString(target, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
    }
    return "저장 완료: " + root.relativize(target);
  }

  @Tool(description = "관리 루트 내부에 디렉토리를 생성합니다. 필요한 부모 디렉토리도 생성합니다.")
  public String createDirectory(@ToolParam(description = "생성할 디렉토리 상대 경로") String path) throws IOException {
    Path target = resolve(path);
    Files.createDirectories(target);
    return "디렉토리 생성 완료: " + root.relativize(target);
  }

  @Tool(description = "관리 루트 내부의 일반 파일을 복사합니다. 대상이 이미 있으면 실패하며 디렉토리는 복사하지 않습니다.")
  public String copyFile(@ToolParam(description = "원본 파일 상대 경로") String source,
      @ToolParam(description = "대상 파일 상대 경로") String destination) throws IOException {
    Path from = regularFile(source);
    Path to = mutablePath(destination);
    Files.copy(from, to);
    return "복사 완료: " + destination;
  }

  @Tool(description = "관리 루트 내부의 일반 파일을 이동하거나 이름을 변경합니다. 대상이 이미 있으면 실패합니다.")
  public String moveFile(@ToolParam(description = "원본 파일 상대 경로") String source,
      @ToolParam(description = "대상 파일 상대 경로") String destination) throws IOException {
    Path from = regularFile(source);
    Path to = mutablePath(destination);
    Files.move(from, to);
    return "이동 완료: " + destination;
  }

  @Tool(description = "관리 루트 내부의 파일 또는 빈 디렉토리를 삭제합니다. 루트와 내용이 있는 디렉토리는 삭제할 수 없습니다.")
  public String deleteFile(@ToolParam(description = "삭제할 파일 또는 빈 디렉토리 상대 경로") String path) throws IOException {
    Files.delete(mutablePath(path));
    return "삭제 완료: " + path;
  }

  private Path regularFile(String path) throws IOException {
    Path target = resolve(path);
    if (!Files.isRegularFile(target, LinkOption.NOFOLLOW_LINKS)) {
      throw new IOException("일반 파일이 아닙니다: " + path);
    }
    return target;
  }

  private Path mutablePath(String path) throws IOException {
    Path target = resolve(path);
    if (target.equals(root)) {
      throw new IllegalArgumentException("관리 루트 자체는 변경하거나 삭제할 수 없습니다.");
    }
    return target;
  }

  private Path resolve(String path) throws IOException {
    if (path == null || path.isBlank()) {
      throw new IllegalArgumentException("루트 기준 상대 경로를 입력하세요. 루트는 .입니다.");
    }
    Path relative = Path.of(path);
    // Windows 드라이브 상대 경로와 NTFS 대체 데이터 스트림도 허용하지 않는다.
    if (relative.isAbsolute() || path.contains(":")) {
      throw new IllegalArgumentException("절대 경로는 사용할 수 없습니다.");
    }
    Path target = root.resolve(relative).normalize();
    if (!target.startsWith(root)) {
      throw new IllegalArgumentException("관리 루트 밖에는 접근할 수 없습니다.");
    }
    // 루트를 생성하기 전에도 부모 경로의 링크를 검사한다.
    for (Path ancestor = root; ancestor != null; ancestor = ancestor.getParent()) {
      if (Files.isSymbolicLink(ancestor)
          || (Files.exists(ancestor, LinkOption.NOFOLLOW_LINKS)
              && !ancestor.toRealPath().equals(ancestor))) {
        throw new IOException("관리 루트의 부모 경로에 링크가 포함되어 있습니다.");
      }
    }
    Files.createDirectories(root);
    if (!root.toRealPath().equals(root)) {
      throw new IOException("관리 루트 경로에 링크 또는 경로 재지정이 포함되어 있습니다.");
    }
    // 존재하는 모든 경로 요소를 확인하여 심볼릭 링크와 Windows junction의 외부 접근을 차단한다.
    Path current = root;
    for (Path part : root.relativize(target)) {
      current = current.resolve(part);
      if (Files.isSymbolicLink(current)) {
        throw new IOException("심볼릭 링크는 사용할 수 없습니다.");
      }
      if (Files.exists(current, LinkOption.NOFOLLOW_LINKS)
          && !current.toRealPath().startsWith(root)) {
        throw new IOException("링크를 통한 관리 루트 외부 접근은 허용되지 않습니다.");
      }
    }
    return target;
  }

  public record Item(String path, boolean directory) {}
}
