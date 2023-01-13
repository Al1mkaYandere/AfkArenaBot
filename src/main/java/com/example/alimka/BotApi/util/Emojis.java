package com.example.alimka.BotApi.util;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Emojis {
    HI(EmojiParser.parseToUnicode(":wave:")),
    COMPUTER(EmojiParser.parseToUnicode(":desktop_computer:")),
    EXPLODING_HEAD(EmojiParser.parseToUnicode(":exploding_head:")),
    PARTY_GUY(EmojiParser.parseToUnicode(":star_struck:")),
    WRONG_MESSAGE(EmojiParser.parseToUnicode(":x:")),
    INNOCENT(EmojiParser.parseToUnicode(":innocent:")),
    SPACE_INVADER(EmojiParser.parseToUnicode(":space_invader:")),
    SCREAM_FACE(EmojiParser.parseToUnicode(":scream:")),
    STAR_STRUCK(EmojiParser.parseToUnicode(":star_struck:")),
    FACE_WITH_MONOCLE(EmojiParser.parseToUnicode(":face_with_monocle:")),
    SUNGLASSES(EmojiParser.parseToUnicode(":sunglasses:")),
    WINK_FACE(EmojiParser.parseToUnicode(":wink:"));

    private String emojiName;
    public String getEmoji() {
        return emojiName;
    }
}
