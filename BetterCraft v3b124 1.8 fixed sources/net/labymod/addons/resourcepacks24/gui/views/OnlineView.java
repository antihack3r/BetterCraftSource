/*
 * Decompiled with CFR 0.152.
 */
package net.labymod.addons.resourcepacks24.gui.views;

import net.labymod.addons.resourcepacks24.api.Resourcepacks24Api;
import net.labymod.addons.resourcepacks24.api.model.Pack;
import net.labymod.addons.resourcepacks24.api.util.enums.EnumFeedType;
import net.labymod.addons.resourcepacks24.api.util.enums.EnumResourcepackType;
import net.labymod.addons.resourcepacks24.api.util.interfaces.ActionResponse;
import net.labymod.addons.resourcepacks24.gui.GuiResourcepacks24;
import net.labymod.addons.resourcepacks24.gui.elements.OnlinePackElement;
import net.labymod.addons.resourcepacks24.gui.views.View;
import net.labymod.gui.elements.GuiImageButton;
import net.labymod.gui.elements.GuiTextboxPrompt;
import net.labymod.main.LabyMod;
import net.labymod.main.ModTextures;
import net.labymod.utils.Consumer;
import net.labymod.utils.DrawUtils;
import net.labymod.utils.texture.DynamicTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class OnlineView
extends View {
    public static final ResourceLocation TEXTURE_ONLINE = new ResourceLocation("resourcepacks24/textures/online.png");
    private GuiImageButton buttonSearch;
    private GuiImageButton buttonSearchRemove;
    private GuiImageButton buttonNextPage;
    private GuiImageButton buttonPreviousPage;
    private GuiImageButton buttonLibrary;
    private AvailableLibrary selectedLibrary = AvailableLibrary.TRENDING;
    private int page;
    private boolean paginator;
    private String query;
    private OnlinePackElement[] onlineRepository = new OnlinePackElement[0];
    private final ActionResponse<Pack[]> actionResponse;

    public OnlineView(final GuiResourcepacks24 gui) {
        super(gui, "Online library");
        final DynamicTextureManager dynamicIconManager = gui.getResourcepacks24().getRp24Api().getDynamicIconManager();
        this.actionResponse = new ActionResponse<Pack[]>(){

            @Override
            public void success(Pack[] packs) {
                OnlinePackElement[] array = new OnlinePackElement[packs.length];
                int i2 = 0;
                while (i2 < packs.length) {
                    array[i2] = new OnlinePackElement(packs[i2], dynamicIconManager, OnlineView.this);
                    ++i2;
                }
                OnlineView.this.onlineRepository = array;
                gui.initGui();
                OnlineView.this.onLoaded(OnlineView.class);
            }

            @Override
            public void failed(String message) {
                OnlineView.this.throwError(message);
            }
        };
        this.buttonSearch = new GuiImageButton(TEXTURE_ONLINE, 0, 0, 127, 127);
        this.buttonSearchRemove = new GuiImageButton(TEXTURE_ONLINE, 0, 127, 127, 127);
        this.buttonNextPage = new GuiImageButton(ModTextures.MISC_ARROW, 127, 0, 127, 255);
        this.buttonPreviousPage = new GuiImageButton(ModTextures.MISC_ARROW, 0, 0, 127, 255);
        this.buttonLibrary = new GuiImageButton(TEXTURE_ONLINE, 127, 0, 127, 127);
        this.loadPacks(1);
    }

    private void loadPacks(int page) {
        Resourcepacks24Api api2 = this.gui.getResourcepacks24().getRp24Api();
        this.paginator = this.selectedLibrary.isPaginator();
        this.page = page;
        this.query = null;
        this.loaded = false;
        this.scrollbar.setScrollY(0.0);
        api2.getDynamicIconManager().unloadAll();
        switch (this.selectedLibrary) {
            case NEW: {
                api2.resourcepacks(EnumResourcepackType.NEW, this.actionResponse);
                break;
            }
            case PACKS_OF_THE_WEEK: {
                api2.feed(EnumFeedType.RESOURCEPACK_OF_THE_WEEK, this.page, this.actionResponse);
                break;
            }
            case PROMOTED: {
                api2.feed(EnumFeedType.PROMOTION, this.page, this.actionResponse);
                break;
            }
            default: {
                api2.feed(EnumFeedType.TRENDING, this.page, this.actionResponse);
            }
        }
    }

    @Override
    public void init(double x2, double y2, double width, double height, double margin) {
        super.init(x2, y2, width, height, margin);
        this.buttonSearch.init(x2 + 2.0, y2 - 12.0, 10.0);
        this.buttonSearchRemove.init(x2 + 2.0 + 14.0, y2 - 12.0, 10.0);
    }

    @Override
    public void renderPre(int mouseX, int mouseY) {
        super.renderPre(mouseX, mouseY);
        if (!this.loaded) {
            return;
        }
        double entryWidth = this.width - (double)(this.scrollbar.isHidden() ? 0 : 5);
        double entryHeight = entryWidth / 7.0;
        double listY = this.y + 3.0 + this.scrollbar.getScrollY();
        OnlinePackElement[] array = this.onlineRepository;
        int index = 0;
        while (index < array.length) {
            this.drawPack(array[index], this.x, listY, entryWidth, entryHeight, mouseX, mouseY);
            listY += entryHeight + 3.0;
            ++index;
        }
        this.postRenderCursorY = listY - this.scrollbar.getScrollY();
    }

    @Override
    public void renderPost(int mouseX, int mouseY) {
        super.renderPost(mouseX, mouseY);
        DrawUtils draw = LabyMod.getInstance().getDrawUtils();
        this.buttonSearch.draw(mouseX, mouseY);
        if (this.paginator) {
            double paginatorX = this.x + this.width - 10.0;
            double paginatorY = this.y + this.height + 4.0;
            String pageString = String.valueOf(this.page);
            int pageStringWidth = draw.getStringWidth(pageString);
            int padding = 5;
            this.buttonPreviousPage.setImageAlpha(this.page <= 1 ? 0.2f : 1.0f);
            this.buttonPreviousPage.setEnabled(this.page > 1);
            this.buttonNextPage.setImageAlpha(this.onlineRepository.length == 0 ? 0.2f : 1.0f);
            this.buttonNextPage.setEnabled(this.onlineRepository.length != 0);
            this.buttonNextPage.draw(paginatorX, paginatorY, 10.0, 8.0, mouseX, mouseY);
            draw.drawString(pageString, paginatorX -= (double)(pageStringWidth + 5), paginatorY);
            this.buttonPreviousPage.draw(paginatorX -= 15.0, paginatorY, 10.0, 8.0, mouseX, mouseY);
        }
        if (this.query == null) {
            this.buttonLibrary.draw(this.x + 2.0, this.y + this.height + 4.0, 10.0, mouseX, mouseY);
            draw.drawString(this.selectedLibrary.getDisplayName(), this.x + 2.0 + 13.0, this.y + this.height + 5.0);
        } else {
            int amount = this.onlineRepository.length;
            String queryTrimmed = draw.trimStringToWidth(this.query, (int)(this.width / 2.0));
            draw.drawString(String.valueOf(amount) + " result" + (amount == 1 ? "" : "s") + " for " + queryTrimmed + (queryTrimmed.equals(this.query) ? "" : "..."), this.x + 2.0, this.y + this.height + 4.0);
            this.buttonSearchRemove.draw(mouseX, mouseY);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.buttonSearch.isMouseOver(mouseX, mouseY)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiTextboxPrompt(this.gui, "Search resourcepack online", "Search", "Cancel", "", new Consumer<String>(){

                @Override
                public void accept(String query) {
                    if (query.replaceAll(" ", "").isEmpty()) {
                        return;
                    }
                    OnlineView.this.query = query;
                    OnlineView.this.gui.getResourcepacks24().getRp24Api().search(query, OnlineView.this.actionResponse);
                    OnlineView.this.paginator = false;
                    OnlineView.this.page = 1;
                    OnlineView.this.scrollbar.setScrollY(0.0);
                }
            }));
        }
        if (this.buttonPreviousPage.isMouseOver(mouseX, mouseY)) {
            this.loadPacks(this.page - 1);
        }
        if (this.buttonNextPage.isMouseOver(mouseX, mouseY)) {
            this.loadPacks(this.page + 1);
        }
        if (this.buttonLibrary.isMouseOver(mouseX, mouseY)) {
            this.selectedLibrary = this.selectedLibrary.next();
            this.loadPacks(1);
        }
        if (this.buttonSearchRemove.isMouseOver(mouseX, mouseY)) {
            this.loadPacks(1);
        }
    }

    private static enum AvailableLibrary {
        TRENDING("Trending", true),
        PROMOTED("Featured", false),
        PACKS_OF_THE_WEEK("Packs of the week", true),
        NEW("New packs", false);

        private String displayName;
        private boolean paginator;

        public AvailableLibrary next() {
            return AvailableLibrary.values()[(this.ordinal() + 1) % AvailableLibrary.values().length];
        }

        public String getDisplayName() {
            return this.displayName;
        }

        public boolean isPaginator() {
            return this.paginator;
        }

        private AvailableLibrary(String displayName, boolean paginator) {
            this.displayName = displayName;
            this.paginator = paginator;
        }
    }
}

