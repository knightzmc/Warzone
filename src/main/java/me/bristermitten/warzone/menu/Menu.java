package me.bristermitten.warzone.menu;

import java.util.List;

public record Menu(List<Page> pages) {

    public List<Page> getPages() {
        return List.copyOf(pages);
    }


}
