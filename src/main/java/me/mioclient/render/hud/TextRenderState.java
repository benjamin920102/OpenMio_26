package me.mioclient.render.hud;

/* compiled from: 0.java */
/* loaded from: mio-yarn.jar:me/mioclient/Helper_22.class */
public class TextRenderState {
    public String text;
    public int num;
    public float x;
    public float y;
    public boolean flag;

    public void initialize(String str, float f, float f2, int i) {
        this.text = str;
        this.x = f;
        this.y = f2;
        this.num = i;
        this.flag = true;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String str) {
        this.text = str;
    }

    public int getSide() {
        return this.num;
    }

    public void setSide(int i) {
        this.num = i;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float f) {
        this.x = f;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float f) {
        this.y = f;
    }

    public boolean isActive() {
        return this.flag;
    }

    public void setActive(boolean z) {
        this.flag = z;
    }
}
