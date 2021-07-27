package me.bristermitten.warzone.config.yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Provider;

public class SnakeYamlProvider implements Provider<Yaml> {
    @Override
    public Yaml get() {
        var options = new DumperOptions();
        options.setIndent(4);
        options.setPrettyFlow(true);
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }
}
