package me.bristermitten.warzone.lang;

import org.jetbrains.annotations.Nullable;

public record LangElement(
        @Nullable String message,
        @Nullable String title,
        @Nullable String subtitle
) {

    public static class LangElementBuilder {
        private @Nullable String message;
        private @Nullable String title;
        private @Nullable String subtitle;

        public LangElementBuilder setMessage(@Nullable String message) {
            this.message = message;
            return this;
        }

        public LangElementBuilder setTitle(@Nullable String title) {
            this.title = title;
            return this;
        }

        public LangElementBuilder setSubtitle(@Nullable String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public LangElement build() {
            return new LangElement(message, title, subtitle);
        }
    }
}
