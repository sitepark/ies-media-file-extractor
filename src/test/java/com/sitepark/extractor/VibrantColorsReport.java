package com.sitepark.extractor;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sitepark.extractor.types.ImageInfo;
import com.sitepark.extractor.values.RgbColor;
import com.sitepark.extractor.values.VibrantColors;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class VibrantColorsReport {

  private static final Path IMAGES_DIR = Paths.get("src/test/resources/files/images");
  private static final String EXPECTED_SUFFIX = ".expected.json";
  private static final Path REPORT_FILE = Paths.get("target/vibrant-colors-report.html");

  static void generate() throws IOException {
    JsonMapper mapper = new JsonMapper();
    List<ImageEntry> entries = new ArrayList<>();
    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(IMAGES_DIR)) {
      for (Path file : dirStream) {
        if (Files.isDirectory(file) || file.toString().endsWith(EXPECTED_SUFFIX)) {
          continue;
        }
        Path json = file.getParent().resolve(file.getFileName() + EXPECTED_SUFFIX);
        if (Files.exists(json)) {
          ImageInfo info = mapper.readValue(json.toFile(), ImageInfo.class);
          entries.add(new ImageEntry(file.getFileName().toString(), info));
        }
      }
    }
    entries.sort(Comparator.comparing(ImageEntry::filename));
    Files.createDirectories(REPORT_FILE.getParent());
    try (PrintWriter out =
        new PrintWriter(Files.newBufferedWriter(REPORT_FILE, StandardCharsets.UTF_8))) {
      writeHtml(out, entries);
      System.out.println("Vibrant colors report generated: file://" + REPORT_FILE.toAbsolutePath());
    }
  }

  private static void writeHtml(PrintWriter out, List<ImageEntry> entries) {
    out.print(
        """
        <!DOCTYPE html>
        <html lang="de">
        <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Vibrant Colors Report</title>
        <style>
          * { box-sizing: border-box; margin: 0; padding: 0; }
          body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
                 background: #f0f2f5; padding: 28px; color: #222; }
          h1 { font-size: 20px; font-weight: 600; margin-bottom: 24px; color: #111; }
          .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
                  gap: 20px; }
          .card { background: #fff; border-radius: 10px; overflow: hidden;
                  box-shadow: 0 1px 4px rgba(0,0,0,.12); }
          .card-img-wrap { width: 100%; height: 160px; background: #e8e8e8;
                           overflow: hidden; position: relative; }
          .card-img-wrap img { width: 100%; height: 100%; object-fit: cover; display: block; }
          .card-img-missing { width: 100%; height: 100%; display: flex; align-items: center;
                              justify-content: center; color: #bbb; font-size: 12px; }
          .card-body { padding: 14px; }
          .card-title { font-size: 12px; font-weight: 600; margin-bottom: 3px;
                        word-break: break-all; }
          .card-meta { font-size: 11px; color: #888; margin-bottom: 12px; }
          .swatches { display: flex; flex-direction: column; gap: 5px; }
          .swatch { display: flex; align-items: center; gap: 8px; }
          .swatch-box { width: 44px; height: 20px; border-radius: 3px; flex-shrink: 0;
                        border: 1px solid rgba(0,0,0,.08); }
          .swatch-label { font-size: 11px; color: #555; width: 86px; flex-shrink: 0; }
          .swatch-hex { font-size: 11px; color: #888; font-family: 'SFMono-Regular',
                        Consolas, monospace; }
          .swatch-null .swatch-label { color: #bbb; }
          .swatch-null .swatch-hex { color: #ccc; }
          .no-colors { font-size: 11px; color: #bbb; font-style: italic; }
        </style>
        </head>
        <body>
        <h1>Vibrant Colors</h1>
        <div class="grid">
        """);

    for (ImageEntry entry : entries) {
      writeCard(out, entry);
    }

    out.print(
        """
        </div>
        </body>
        </html>
        """);
  }

  private static void writeCard(PrintWriter out, ImageEntry entry) {
    String filename = entry.filename();
    ImageInfo info = entry.info();
    String imgSrc = "../src/test/resources/files/images/" + filename;

    out.printf("<div class=\"card\">%n");
    out.printf(
        "<div class=\"card-img-wrap\">"
            + "<img src=\"%s\" alt=\"%s\""
            + " onerror=\"this.style.display='none';"
            + "this.parentElement.innerHTML='<div class=\\'card-img-missing\\'>kein Vorschaubild"
            + "</div>';\">"
            + "</div>%n",
        imgSrc, filename);
    out.printf("<div class=\"card-body\">%n");
    out.printf("<div class=\"card-title\">%s</div>%n", filename);
    out.printf(
        "<div class=\"card-meta\">%d × %d%s</div>%n",
        info.width(), info.height(), info.hasAlpha() ? " &middot; alpha" : "");

    if (info.vibrantColors() != null) {
      out.printf("<div class=\"swatches\">%n");
      writeSwatches(out, info.vibrantColors());
      out.printf("</div>%n");
    } else {
      out.printf("<div class=\"no-colors\">keine Farbanalyse</div>%n");
    }

    out.printf("</div>%n</div>%n");
  }

  private static void writeSwatches(PrintWriter out, VibrantColors vc) {
    writeSwatch(out, "average", vc.average());
    writeSwatch(out, "dominant", vc.dominant());
    writeSwatch(out, "vibrant", vc.vibrant());
    writeSwatch(out, "muted", vc.muted());
    writeSwatch(out, "darkVibrant", vc.darkVibrant());
    writeSwatch(out, "darkMuted", vc.darkMuted());
    writeSwatch(out, "lightVibrant", vc.lightVibrant());
    writeSwatch(out, "lightMuted", vc.lightMuted());
  }

  private static void writeSwatch(PrintWriter out, String label, RgbColor color) {
    if (color == null) {
      out.printf(
          "<div class=\"swatch swatch-null\">"
              + "<span class=\"swatch-box\" style=\"background:#eee\"></span>"
              + "<span class=\"swatch-label\">%s</span>"
              + "<span class=\"swatch-hex\">–</span>"
              + "</div>%n",
          label);
    } else {
      String hex = String.format("#%02X%02X%02X", color.red(), color.green(), color.blue());
      String rgb = String.format("rgb(%d,%d,%d)", color.red(), color.green(), color.blue());
      out.printf(
          "<div class=\"swatch\">"
              + "<span class=\"swatch-box\" style=\"background:%s\"></span>"
              + "<span class=\"swatch-label\">%s</span>"
              + "<span class=\"swatch-hex\">%s</span>"
              + "</div>%n",
          rgb, label, hex);
    }
  }

  private record ImageEntry(String filename, ImageInfo info) {}
}
