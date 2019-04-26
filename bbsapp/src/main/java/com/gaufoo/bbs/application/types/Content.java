package com.gaufoo.bbs.application.types;

import java.util.List;

public interface Content {
    List<ContentItem> getItems();

    interface ContentItem {}
    interface Picture extends ContentItem {
        String getUrl();
    }
    interface Paragraph extends ContentItem {
        String getText();
    }

    class ContentInput {
        public List<ContentElemInput> elems;
    }
    class ContentElemInput {
        public ElemType type;
        public String str;
    }
    enum ElemType {
        Picture,
        Text
    }

}
