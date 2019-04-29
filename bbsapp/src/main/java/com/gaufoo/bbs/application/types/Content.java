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

        @Override
        public String toString() {
            return "ContentInput{" +
                    "elems=" + elems +
                    '}';
        }
    }
    class ContentElemInput {
        public ElemType type;
        public String str;

        @Override
        public String toString() {
            return "ContentElemInput{" +
                    "type=" + type +
                    ", str='" + str + '\'' +
                    '}';
        }
    }
    enum ElemType {
        Picture,
        Text
    }

}
