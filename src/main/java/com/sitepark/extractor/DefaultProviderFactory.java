package com.sitepark.extractor;

import com.sitepark.extractor.provider.doc.DocInfoProvider;
import com.sitepark.extractor.provider.image.ComDrewImageMetadataReader;
import com.sitepark.extractor.provider.image.ImageInfoProvider;
import com.sitepark.extractor.provider.image.VipsExtractor;
import com.sitepark.vips.manager.VipsClient;
import com.sitepark.vips.manager.VipsClientPool;
import java.io.IOException;
import java.util.List;

/**
 * Factory that creates the default set of {@link FileInfoProvider} implementations.
 *
 * <p>Creates a {@link com.sitepark.extractor.provider.doc.DocInfoProvider} for document formats
 * and an {@link com.sitepark.extractor.provider.image.ImageInfoProvider} for image formats.
 *
 * <p>The libvips client pool is created lazily on the first call and shared across all providers.
 * Its behaviour can be customised via system properties:
 * <ul>
 *   <li>{@code com.sitepark.extractor.DefaultProviderFactory.vips.inprocess} – set to
 *       {@code "true"} to run vips in-process instead of as a separate process</li>
 *   <li>{@code com.sitepark.extractor.provider.VipsIpcMetadataReader.vipsClient.priority} –
 *       Unix nice level for the vips process (default: {@link VipsClientPool#DEFAULT_NICE_LEVEL})</li>
 * </ul>
 */
public class DefaultProviderFactory {

  private static VipsClientPool vipsClientPool;

  private static final String VIPS_IN_PROCESS =
      "com.sitepark.extractor.DefaultProviderFactory.vips.inprocess";

  /**
   * Creates the default list of providers: one for documents, one for images.
   *
   * @return an unmodifiable list of {@link FileInfoProvider} implementations
   */
  public static List<FileInfoProvider<?>> createProviders() {
    return List.of(createDocInfoProvider(), createImageInfoProvider());
  }

  private static DocInfoProvider createDocInfoProvider() {
    return new DocInfoProvider();
  }

  private static ImageInfoProvider createImageInfoProvider() {
    return new ImageInfoProvider(
        new ComDrewImageMetadataReader(), new VipsExtractor(getOrCreateVipsClientPool()));
  }

  private static synchronized VipsClientPool getOrCreateVipsClientPool() {
    if (vipsClientPool == null) {
      int priority =
          Integer.getInteger(
              "com.sitepark.extractor.provider.VipsIpcMetadataReader.vipsClient.priority", 19);

      boolean inProcess = "true".equals(System.getProperty(VIPS_IN_PROCESS));

      if (priority == VipsClientPool.DEFAULT_NICE_LEVEL && !inProcess) {
        vipsClientPool = VipsClientPool.getDefault();
      } else {
        try {
          vipsClientPool =
              VipsClient.builder()
                  .niceLevel(priority)
                  .inProcess(inProcess)
                  .buildPool(Runtime.getRuntime().availableProcessors());
        } catch (IOException e) {
          throw new ExtractorCreateException("Unable to create VIPS client pool", e);
        }
      }
    }
    return vipsClientPool;
  }
}
