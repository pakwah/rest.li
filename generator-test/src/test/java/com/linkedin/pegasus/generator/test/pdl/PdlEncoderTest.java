package com.linkedin.pegasus.generator.test.pdl;

import com.linkedin.data.schema.AbstractSchemaParser;
import com.linkedin.data.schema.DataSchema;
import com.linkedin.data.schema.DataSchemaResolver;
import com.linkedin.data.schema.SchemaToPdlEncoder;
import com.linkedin.data.schema.grammar.PdlSchemaParser;
import com.linkedin.data.schema.resolver.MultiFormatDataSchemaResolver;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class PdlEncoderTest extends GeneratorTest
{
  private final File pegasusSrcDir = new File(System.getProperty("testDir") + "/pegasus");
  private final DataSchemaResolver resolver = MultiFormatDataSchemaResolver.withBuiltinFormats(pegasusSrcDir.getAbsolutePath());

  @DataProvider(name = "pdlFilePaths")
  private Object[][] providePdlFilePaths()
  {
    return new Object[][]
        {
            { "arrays.AnonArray" },
            { "arrays.WithPrimitivesArray" },
            { "denormalized.WithNamespacedDeclarations" },
            { "denormalized.WithIncludeDeclaration" },
            { "deprecated.DeprecatedRecord" },
            { "enums.Fruits" },
            { "enums.EnumProperties" },
            { "enums.DeprecatedSymbols" },
            { "escaping.PdlKeywordEscaping" },
            { "fixed.Fixed8" },
            { "maps.WithOrders" },
            { "maps.WithPrimitivesMap" },
            { "records.Note" },
            { "records.WithAliases" },
            { "records.WithInclude" },
            { "records.WithIncludeAfter" },
            { "records.WithInlineRecord" },
            { "records.WithPrimitives" },
            { "records.WithOptionalPrimitives" },
            { "records.NumericDefaults" },
            { "records.WithComplexTypeDefaults" },
            { "typerefs.UnionWithInlineRecord" },
            { "typerefs.MapTyperef" },
            { "typerefs.IntTyperef" },
            { "unions.WithPrimitivesUnion" },
            { "unions.WithAliases" }
        };
  }

  /**
   * Validate {@link SchemaToPdlEncoder} by parsing a variety of .pdl files, encoding them back to source, and
   * verifying that the re-encoded source matches the original file.
   */
  @Test(dataProvider = "pdlFilePaths")
  public void testEncode(String pdlFilePath) throws IOException
  {
    assertRoundTrip(pdlFilePath);
  }

  private DataSchema parseSchema(File file) throws IOException
  {
    AbstractSchemaParser parser = new PdlSchemaParser(resolver);
    parser.parse(new FileInputStream(file));
    StringBuilder errorMessageBuilder = parser.errorMessageBuilder();
    if (errorMessageBuilder.length() > 0)
    {
      fail(
          "Failed to parse schema: " + file.getAbsolutePath() + "\nerrors: " + errorMessageBuilder.toString());
      System.exit(1);
    }
    if (parser.topLevelDataSchemas().size() != 1)
    {
      fail(
          "Failed to parse any schemas from: " + file.getAbsolutePath() + "\nerrors: " + errorMessageBuilder.toString());
      System.exit(1);
    }
    return parser.topLevelDataSchemas().get(0);
  }

  private void assertRoundTrip(String relativeName) throws IOException
  {
    String fullName = "com.linkedin.pegasus.generator.test.idl." + relativeName;
    File file = new File(pegasusSrcDir, "/" + fullName.replace('.', '/') + ".pdl");

    DataSchema parsed = parseSchema(file);
    String original = loadSchema(file);
    assertNotNull(parsed, "Failed to resolve: " + fullName + " resolver path: " + pegasusSrcDir.getAbsolutePath());

    StringWriter writer = new StringWriter();
    SchemaToPdlEncoder encoder = new SchemaToPdlEncoder(writer);
    encoder.setTypeReferenceFormat(SchemaToPdlEncoder.TypeReferenceFormat.PRESERVE);
    encoder.encode(parsed);
    String encoded = writer.toString();
    assertEqualsIgnoringSpacing(encoded, original);
  }

  private void assertEqualsIgnoringSpacing(String lhs, String rhs)
  {
    assertEquals(canonicalize(lhs), canonicalize(rhs));
  }

  private String loadSchema(File file)
  {
    try
    {
      return FileUtils.readFileToString(file);
    }
    catch (IOException e)
    {
      fail("Failed to load file: " + file + ": " + e.getMessage());
      return null;
    }
  }

  private String canonicalize(String pdlSource)
  {
    return pdlSource
        .replaceAll("([{}\\[\\]\\?=:])", " $1 ") // force spacing around grammatical symbols
        .replaceAll(",", " ") // commas are insignificant in pdl, strip them out
        .replaceAll("\\s+", " ").trim(); // canonicalize spacing
  }
}