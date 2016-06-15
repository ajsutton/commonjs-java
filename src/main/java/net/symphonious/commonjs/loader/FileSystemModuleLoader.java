package net.symphonious.commonjs.loader;

import net.symphonious.commonjs.ModuleLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * A simple ModuleLoader implementation that loads modules from the local filesystem.
 *
 * <p><strong>Warning:</strong> no validation is done to verify that required modules have not maliciously attempted to load files from outside of {@literal baseDir}.
 * This class should not be used to compile user-supplied modules.</p>
 *
 * <p>Modules are expected to be in files with the same sub-path under {@literal baseDir} as their module ID with the suffix {@literal .js}. For example the module {@literal lib/myModule}
 * should be in the file {@literal <baseDir>/lib/myModule.js}</p>
 */
public class FileSystemModuleLoader implements ModuleLoader
{
    private final Path baseDir;
    private final Charset charset;

    /** Create a new loader that loads files from under {@literal baseDir} using the character set {@literal charset}.
     *
     * @param baseDir the directory to load module from.
     * @param charset the character set to use when reading files.
     */
    public FileSystemModuleLoader(final File baseDir, final Charset charset)
    {
        this(baseDir.toPath(), charset);
    }

    /** Create a new loader that loads files from under {@literal baseDir} using the character set {@literal charset}.
     *
     * @param baseDir the directory to load module from.
     * @param charset the character set to use when reading files.
     */
    public FileSystemModuleLoader(final Path baseDir, final Charset charset)
    {
        this.baseDir = baseDir;
        this.charset = charset;
    }

    @Override
    public String loadModule(final String moduleId) throws IOException
    {
        try
        {
            return new String(Files.readAllBytes(baseDir.resolve(moduleId + ".js")), charset);
        }
        catch (final NoSuchFileException e)
        {
            return null;
        }
    }
}
