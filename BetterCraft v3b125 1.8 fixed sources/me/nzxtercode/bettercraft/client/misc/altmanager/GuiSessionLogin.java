/*
 * Decompiled with CFR 0.152.
 */
package me.nzxtercode.bettercraft.client.misc.altmanager;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.lwjgl.input.Keyboard;

public class GuiSessionLogin
extends GuiScreen {
    private GuiTextField tokenField;
    private GuiScreen parent;

    public GuiSessionLogin(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height / 4 + 92 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, width / 2 - 100, height / 4 + 116 + 12, "Back"));
        this.tokenField = new GuiTextField(0, this.fontRendererObj, width / 2 - 100, 70, 200, 20);
        this.tokenField.setMaxStringLength(Integer.MAX_VALUE);
        this.tokenField.setFocused(true);
    }

    @Override
    public void keyTyped(char character, int keyCode) throws IOException {
        this.tokenField.textboxKeyTyped(character, keyCode);
        if (keyCode == 15) {
            this.tokenField.setFocused(!this.tokenField.isFocused());
        }
        if (keyCode == 28) {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.tokenField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                if (!this.tokenField.getText().isEmpty()) {
                    String content = this.tokenField.getText();
                    String token = content.split(":").length > 0 ? content.split(":")[content.startsWith("token:") ? 1 : 0] : content;
                    try {
                        this.sendGetRequest("https://api.minecraftservices.com/minecraft/profile", request -> request.addHeader("Authorization", String.format("Bearer %s", token)), response -> {
                            try {
                                JsonObject json = new JsonParser().parse(EntityUtils.toString(response.getEntity())).getAsJsonObject();
                                UUID uuid = UUID.fromString(json.get("id").getAsString().replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
                                String name = json.get("name").getAsString();
                                Minecraft.getMinecraft();
                                Minecraft.setSession(new Session(name, uuid.toString(), token, "mojang"));
                            }
                            catch (IOException exception) {
                                throw new RuntimeException(exception);
                            }
                        });
                    }
                    catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
                this.mc.displayGuiScreen(this.parent);
                break;
            }
            case 1: {
                this.mc.displayGuiScreen(this.parent);
            }
        }
    }

    private void sendGetRequest(String url, Consumer<HttpGet> request, Consumer<HttpResponse> response) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        request.accept(httpGet);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        response.accept(httpResponse);
        httpResponse.close();
        httpClient.close();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void updateScreen() {
        this.tokenField.updateCursorCounter();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiSessionLogin.drawCenteredString(this.mc.fontRendererObj, "Session Login", width / 2, 20, -1);
        this.mc.fontRendererObj.drawString(String.valueOf(EnumChatFormatting.GRAY.toString()) + "Token", width / 2 - 100, 57, -1);
        this.tokenField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}

