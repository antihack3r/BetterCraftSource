/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.user.emote.keys;

import java.beans.ConstructorProperties;
import net.labymod.user.emote.keys.EmotePose;

public class EmoteKeyFrame {
    private long offset;
    private EmotePose[] emotePoses;

    public EmoteKeyFrame clone() {
        EmotePose[] emotePosesCopied = new EmotePose[this.emotePoses.length];
        int t2 = 0;
        EmotePose[] emotePoseArray = this.emotePoses;
        int n2 = this.emotePoses.length;
        int n3 = 0;
        while (n3 < n2) {
            EmotePose emotePose = emotePoseArray[n3];
            emotePosesCopied[t2] = new EmotePose(emotePose.getBodyPart(), emotePose.getX(), emotePose.getY(), emotePose.getZ(), emotePose.isInterpolate());
            ++t2;
            ++n3;
        }
        return new EmoteKeyFrame(this.offset, emotePosesCopied);
    }

    public long getOffset() {
        return this.offset;
    }

    public EmotePose[] getEmotePoses() {
        return this.emotePoses;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setEmotePoses(EmotePose[] emotePoses) {
        this.emotePoses = emotePoses;
    }

    @ConstructorProperties(value={"offset", "emotePoses"})
    public EmoteKeyFrame(long offset, EmotePose[] emotePoses) {
        this.offset = offset;
        this.emotePoses = emotePoses;
    }
}

