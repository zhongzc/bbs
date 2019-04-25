package com.gaufoo.bbs.application;

import com.gaufoo.bbs.components.content.common.ContentElem;
import com.gaufoo.bbs.components.content.common.ContentFig;
import com.gaufoo.bbs.components.content.common.ContentInfo;
import com.gaufoo.bbs.components.content.common.ContentParag;

import java.util.Base64;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gaufoo.bbs.application.ComponentFactory.componentFactory;

public class AppContent {
    public static ContentInfo consContent(com.gaufoo.bbs.application.types.Content.ContentInput contentInput) {
        Stream<ContentElem> items = contentInput.inputs.stream().map(i -> {
            if (i instanceof com.gaufoo.bbs.application.types.Content.PictureInput) {
                com.gaufoo.bbs.application.types.Content.PictureInput pic = (com.gaufoo.bbs.application.types.Content.PictureInput) i;
                return componentFactory.contentImages.createFile(Base64.getDecoder().decode(pic.base64Picture))
                        .map(fid -> ContentFig.of(fid.value))
                        .orElse(null);
            } else {
                com.gaufoo.bbs.application.types.Content.ParagraphInput pag = (com.gaufoo.bbs.application.types.Content.ParagraphInput) i;
                return ContentParag.of(pag.text);
            }
        }).filter(Objects::nonNull);

        return ContentInfo.of(items.collect(Collectors.toList()));
    }
}
