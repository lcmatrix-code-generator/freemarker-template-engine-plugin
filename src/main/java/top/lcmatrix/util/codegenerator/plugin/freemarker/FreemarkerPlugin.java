package top.lcmatrix.util.codegenerator.plugin.freemarker;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import top.lcmatrix.util.codegenerator.common.plugin.AbstractTemplateEnginePlugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class FreemarkerPlugin extends AbstractTemplateEnginePlugin {

    private Configuration configuration;
    private StringTemplateLoader stl;

    public FreemarkerPlugin() {
        configuration = new Configuration(Configuration.VERSION_2_3_28);
        stl = new StringTemplateLoader();
        configuration.setTemplateLoader(stl);
    }

    @Override
    public byte[] apply(String s, Object model) {
        Template template = null;
        try {
            template = configuration.getTemplate(s);
        } catch (IOException e) {
        }
        if(template == null){
            configuration.clearTemplateCache();
            stl.putTemplate(s, s);
            try {
                template = configuration.getTemplate(s);
            } catch (IOException e) {
                getLogger().error("template load error.", e);
                throw new RuntimeException("template load error.", e);
            }
        }
        if(template == null){
            return null;
        }
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteOutputStream);
        try {
            template.process(model, outputStreamWriter);
            outputStreamWriter.flush();
            return byteOutputStream.toByteArray();
        } catch (TemplateException | IOException e) {
            getLogger().error("apply template error.", e);
            throw new RuntimeException("apply template error.", e);
        }
    }

    @Override
    public byte[] apply(File templateFile, Object model) {
        String templateId = templateFile.toURI().toString();
        Template template = null;
        try {
            template = configuration.getTemplate(templateId);
        } catch (IOException e) {
        }
        if(template == null){
            configuration.clearTemplateCache();
            try {
                stl.putTemplate(templateId, FileUtils.readFileToString(templateFile, Charset.defaultCharset()));
                template = configuration.getTemplate(templateId);
            } catch (IOException e) {
                getLogger().error("template load error.", e);
                throw new RuntimeException("template load error.", e);
            }
        }
        if(template == null){
            return null;
        }
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(byteOutputStream);
        try {
            template.process(model, outputStreamWriter);
            outputStreamWriter.flush();
            return byteOutputStream.toByteArray();
        } catch (TemplateException | IOException e) {
            getLogger().error("apply template error.", e);
            throw new RuntimeException("apply template error.", e);
        }
    }
}
