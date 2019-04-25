package com.gaufoo.bbs.application.types;

import java.util.List;

public interface Content {
    List<ContentItem> getItems();

    interface ContentItem {}
    interface Picture extends ContentItem {
        String getURL();
    }
    interface Paragraph extends ContentItem {
        String getText();
    }

    class ContentInput {
        public List<ContentItemInput> inputs;
    }
    interface ContentItemInput {}
    class PictureInput implements ContentItemInput {
        public String base64Picture;
    }
    class ParagraphInput implements ContentItemInput {
        public String text;
    }
}
