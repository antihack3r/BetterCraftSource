// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import javax.annotation.Nullable;
import java.util.Iterator;
import net.minecraft.util.text.event.ClickEvent;
import com.google.common.collect.Lists;
import net.minecraft.util.text.TextComponentString;
import com.google.gson.JsonParseException;
import net.minecraft.item.ItemWrittenBook;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ChatAllowedCharacters;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.text.ITextComponent;
import java.util.List;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.Logger;

public class GuiScreenBook extends GuiScreen
{
    private static final Logger LOGGER;
    private static final ResourceLocation BOOK_GUI_TEXTURES;
    private final EntityPlayer editingPlayer;
    private final ItemStack bookObj;
    private final boolean bookIsUnsigned;
    private boolean bookIsModified;
    private boolean bookGettingSigned;
    private int updateCount;
    private final int bookImageWidth = 192;
    private final int bookImageHeight = 192;
    private int bookTotalPages;
    private int currPage;
    private NBTTagList bookPages;
    private String bookTitle;
    private List<ITextComponent> cachedComponents;
    private int cachedPage;
    private NextPageButton buttonNextPage;
    private NextPageButton buttonPreviousPage;
    private GuiButton buttonDone;
    private GuiButton buttonSign;
    private GuiButton buttonFinalize;
    private GuiButton buttonCancel;
    
    static {
        LOGGER = LogManager.getLogger();
        BOOK_GUI_TEXTURES = new ResourceLocation("textures/gui/book.png");
    }
    
    public GuiScreenBook(final EntityPlayer player, final ItemStack book, final boolean isUnsigned) {
        this.bookTotalPages = 1;
        this.bookTitle = "";
        this.cachedPage = -1;
        this.editingPlayer = player;
        this.bookObj = book;
        this.bookIsUnsigned = isUnsigned;
        if (book.hasTagCompound()) {
            final NBTTagCompound nbttagcompound = book.getTagCompound();
            this.bookPages = nbttagcompound.getTagList("pages", 8).copy();
            this.bookTotalPages = this.bookPages.tagCount();
            if (this.bookTotalPages < 1) {
                this.bookTotalPages = 1;
            }
        }
        if (this.bookPages == null && isUnsigned) {
            (this.bookPages = new NBTTagList()).appendTag(new NBTTagString(""));
            this.bookTotalPages = 1;
        }
    }
    
    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.updateCount;
    }
    
    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        if (this.bookIsUnsigned) {
            this.buttonSign = this.addButton(new GuiButton(3, GuiScreenBook.width / 2 - 100, 196, 98, 20, I18n.format("book.signButton", new Object[0])));
            this.buttonDone = this.addButton(new GuiButton(0, GuiScreenBook.width / 2 + 2, 196, 98, 20, I18n.format("gui.done", new Object[0])));
            this.buttonFinalize = this.addButton(new GuiButton(5, GuiScreenBook.width / 2 - 100, 196, 98, 20, I18n.format("book.finalizeButton", new Object[0])));
            this.buttonCancel = this.addButton(new GuiButton(4, GuiScreenBook.width / 2 + 2, 196, 98, 20, I18n.format("gui.cancel", new Object[0])));
        }
        else {
            this.buttonDone = this.addButton(new GuiButton(0, GuiScreenBook.width / 2 - 100, 196, 200, 20, I18n.format("gui.done", new Object[0])));
        }
        final int i = (GuiScreenBook.width - 192) / 2;
        final int j = 2;
        this.buttonNextPage = this.addButton(new NextPageButton(1, i + 120, 156, true));
        this.buttonPreviousPage = this.addButton(new NextPageButton(2, i + 38, 156, false));
        this.updateButtons();
    }
    
    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }
    
    private void updateButtons() {
        this.buttonNextPage.visible = (!this.bookGettingSigned && (this.currPage < this.bookTotalPages - 1 || this.bookIsUnsigned));
        this.buttonPreviousPage.visible = (!this.bookGettingSigned && this.currPage > 0);
        this.buttonDone.visible = (!this.bookIsUnsigned || !this.bookGettingSigned);
        if (this.bookIsUnsigned) {
            this.buttonSign.visible = !this.bookGettingSigned;
            this.buttonCancel.visible = this.bookGettingSigned;
            this.buttonFinalize.visible = this.bookGettingSigned;
            this.buttonFinalize.enabled = !this.bookTitle.trim().isEmpty();
        }
    }
    
    private void sendBookToServer(final boolean publish) throws IOException {
        if (this.bookIsUnsigned && this.bookIsModified && this.bookPages != null) {
            while (this.bookPages.tagCount() > 1) {
                final String s = this.bookPages.getStringTagAt(this.bookPages.tagCount() - 1);
                if (!s.isEmpty()) {
                    break;
                }
                this.bookPages.removeTag(this.bookPages.tagCount() - 1);
            }
            if (this.bookObj.hasTagCompound()) {
                final NBTTagCompound nbttagcompound = this.bookObj.getTagCompound();
                nbttagcompound.setTag("pages", this.bookPages);
            }
            else {
                this.bookObj.setTagInfo("pages", this.bookPages);
            }
            String s2 = "MC|BEdit";
            if (publish) {
                s2 = "MC|BSign";
                this.bookObj.setTagInfo("author", new NBTTagString(this.editingPlayer.getName()));
                this.bookObj.setTagInfo("title", new NBTTagString(this.bookTitle.trim()));
            }
            final PacketBuffer packetbuffer = new PacketBuffer(Unpooled.buffer());
            packetbuffer.writeItemStackToBuffer(this.bookObj);
            this.mc.getConnection().sendPacket(new CPacketCustomPayload(s2, packetbuffer));
        }
    }
    
    @Override
    protected void actionPerformed(final GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 0) {
                this.mc.displayGuiScreen(null);
                this.sendBookToServer(false);
            }
            else if (button.id == 3 && this.bookIsUnsigned) {
                this.bookGettingSigned = true;
            }
            else if (button.id == 1) {
                if (this.currPage < this.bookTotalPages - 1) {
                    ++this.currPage;
                }
                else if (this.bookIsUnsigned) {
                    this.addNewPage();
                    if (this.currPage < this.bookTotalPages - 1) {
                        ++this.currPage;
                    }
                }
            }
            else if (button.id == 2) {
                if (this.currPage > 0) {
                    --this.currPage;
                }
            }
            else if (button.id == 5 && this.bookGettingSigned) {
                this.sendBookToServer(true);
                this.mc.displayGuiScreen(null);
            }
            else if (button.id == 4 && this.bookGettingSigned) {
                this.bookGettingSigned = false;
            }
            this.updateButtons();
        }
    }
    
    private void addNewPage() {
        if (this.bookPages != null && this.bookPages.tagCount() < 50) {
            this.bookPages.appendTag(new NBTTagString(""));
            ++this.bookTotalPages;
            this.bookIsModified = true;
        }
    }
    
    @Override
    protected void keyTyped(final char typedChar, final int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (this.bookIsUnsigned) {
            if (this.bookGettingSigned) {
                this.keyTypedInTitle(typedChar, keyCode);
            }
            else {
                this.keyTypedInBook(typedChar, keyCode);
            }
        }
    }
    
    private void keyTypedInBook(final char typedChar, final int keyCode) {
        if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            this.pageInsertIntoCurrent(GuiScreen.getClipboardString());
        }
        else {
            switch (keyCode) {
                case 14: {
                    final String s = this.pageGetCurrent();
                    if (!s.isEmpty()) {
                        this.pageSetCurrent(s.substring(0, s.length() - 1));
                    }
                    return;
                }
                case 28:
                case 156: {
                    this.pageInsertIntoCurrent("\n");
                    return;
                }
                default: {
                    if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                        this.pageInsertIntoCurrent(Character.toString(typedChar));
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private void keyTypedInTitle(final char p_146460_1_, final int p_146460_2_) throws IOException {
        switch (p_146460_2_) {
            case 14: {
                if (!this.bookTitle.isEmpty()) {
                    this.bookTitle = this.bookTitle.substring(0, this.bookTitle.length() - 1);
                    this.updateButtons();
                }
                return;
            }
            case 28:
            case 156: {
                if (!this.bookTitle.isEmpty()) {
                    this.sendBookToServer(true);
                    this.mc.displayGuiScreen(null);
                }
                return;
            }
            default: {
                if (this.bookTitle.length() < 16 && ChatAllowedCharacters.isAllowedCharacter(p_146460_1_)) {
                    this.bookTitle = String.valueOf(this.bookTitle) + Character.toString(p_146460_1_);
                    this.updateButtons();
                    this.bookIsModified = true;
                }
            }
        }
    }
    
    private String pageGetCurrent() {
        return (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) ? this.bookPages.getStringTagAt(this.currPage) : "";
    }
    
    private void pageSetCurrent(final String p_146457_1_) {
        if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
            this.bookPages.set(this.currPage, new NBTTagString(p_146457_1_));
            this.bookIsModified = true;
        }
    }
    
    private void pageInsertIntoCurrent(final String p_146459_1_) {
        final String s = this.pageGetCurrent();
        final String s2 = String.valueOf(s) + p_146459_1_;
        final int i = this.fontRendererObj.splitStringWidth(String.valueOf(s2) + TextFormatting.BLACK + "_", 118);
        if (i <= 128 && s2.length() < 256) {
            this.pageSetCurrent(s2);
        }
    }
    
    @Override
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(GuiScreenBook.BOOK_GUI_TEXTURES);
        final int i = (GuiScreenBook.width - 192) / 2;
        final int j = 2;
        this.drawTexturedModalRect(i, 2, 0, 0, 192, 192);
        if (this.bookGettingSigned) {
            String s = this.bookTitle;
            if (this.bookIsUnsigned) {
                if (this.updateCount / 6 % 2 == 0) {
                    s = String.valueOf(s) + TextFormatting.BLACK + "_";
                }
                else {
                    s = String.valueOf(s) + TextFormatting.GRAY + "_";
                }
            }
            final String s2 = I18n.format("book.editTitle", new Object[0]);
            final int k = this.fontRendererObj.getStringWidth(s2);
            this.fontRendererObj.drawString(s2, i + 36 + (116 - k) / 2, 34, 0);
            final int l = this.fontRendererObj.getStringWidth(s);
            this.fontRendererObj.drawString(s, i + 36 + (116 - l) / 2, 50, 0);
            final String s3 = I18n.format("book.byAuthor", this.editingPlayer.getName());
            final int i2 = this.fontRendererObj.getStringWidth(s3);
            this.fontRendererObj.drawString(TextFormatting.DARK_GRAY + s3, i + 36 + (116 - i2) / 2, 60, 0);
            final String s4 = I18n.format("book.finalizeWarning", new Object[0]);
            this.fontRendererObj.drawSplitString(s4, i + 36, 82, 116, 0);
        }
        else {
            final String s5 = I18n.format("book.pageIndicator", this.currPage + 1, this.bookTotalPages);
            String s6 = "";
            if (this.bookPages != null && this.currPage >= 0 && this.currPage < this.bookPages.tagCount()) {
                s6 = this.bookPages.getStringTagAt(this.currPage);
            }
            if (this.bookIsUnsigned) {
                if (this.fontRendererObj.getBidiFlag()) {
                    s6 = String.valueOf(s6) + "_";
                }
                else if (this.updateCount / 6 % 2 == 0) {
                    s6 = String.valueOf(s6) + TextFormatting.BLACK + "_";
                }
                else {
                    s6 = String.valueOf(s6) + TextFormatting.GRAY + "_";
                }
            }
            else if (this.cachedPage != this.currPage) {
                if (ItemWrittenBook.validBookTagContents(this.bookObj.getTagCompound())) {
                    try {
                        final ITextComponent itextcomponent = ITextComponent.Serializer.jsonToComponent(s6);
                        this.cachedComponents = ((itextcomponent != null) ? GuiUtilRenderComponents.splitText(itextcomponent, 116, this.fontRendererObj, true, true) : null);
                    }
                    catch (final JsonParseException var13) {
                        this.cachedComponents = null;
                    }
                }
                else {
                    final TextComponentString textcomponentstring = new TextComponentString(TextFormatting.DARK_RED + "* Invalid book tag *");
                    this.cachedComponents = (List<ITextComponent>)Lists.newArrayList((Iterable<?>)textcomponentstring);
                }
                this.cachedPage = this.currPage;
            }
            final int j2 = this.fontRendererObj.getStringWidth(s5);
            this.fontRendererObj.drawString(s5, i - j2 + 192 - 44, 18, 0);
            if (this.cachedComponents == null) {
                this.fontRendererObj.drawSplitString(s6, i + 36, 34, 116, 0);
            }
            else {
                for (int k2 = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.cachedComponents.size()), l2 = 0; l2 < k2; ++l2) {
                    final ITextComponent itextcomponent2 = this.cachedComponents.get(l2);
                    this.fontRendererObj.drawString(itextcomponent2.getUnformattedText(), i + 36, 34 + l2 * this.fontRendererObj.FONT_HEIGHT, 0);
                }
                final ITextComponent itextcomponent3 = this.getClickedComponentAt(mouseX, mouseY);
                if (itextcomponent3 != null) {
                    this.handleComponentHover(itextcomponent3, mouseX, mouseY);
                }
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException {
        if (mouseButton == 0) {
            final ITextComponent itextcomponent = this.getClickedComponentAt(mouseX, mouseY);
            if (itextcomponent != null && this.handleComponentClick(itextcomponent)) {
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
    public boolean handleComponentClick(final ITextComponent component) {
        final ClickEvent clickevent = component.getStyle().getClickEvent();
        if (clickevent == null) {
            return false;
        }
        if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
            final String s = clickevent.getValue();
            try {
                final int i = Integer.parseInt(s) - 1;
                if (i >= 0 && i < this.bookTotalPages && i != this.currPage) {
                    this.currPage = i;
                    this.updateButtons();
                    return true;
                }
            }
            catch (final Throwable t) {}
            return false;
        }
        final boolean flag = super.handleComponentClick(component);
        if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.mc.displayGuiScreen(null);
        }
        return flag;
    }
    
    @Nullable
    public ITextComponent getClickedComponentAt(final int p_175385_1_, final int p_175385_2_) {
        if (this.cachedComponents == null) {
            return null;
        }
        final int i = p_175385_1_ - (GuiScreenBook.width - 192) / 2 - 36;
        final int j = p_175385_2_ - 2 - 16 - 16;
        if (i < 0 || j < 0) {
            return null;
        }
        final int k = Math.min(128 / this.fontRendererObj.FONT_HEIGHT, this.cachedComponents.size());
        if (i <= 116 && j < this.mc.fontRendererObj.FONT_HEIGHT * k + k) {
            final int l = j / this.mc.fontRendererObj.FONT_HEIGHT;
            if (l >= 0 && l < this.cachedComponents.size()) {
                final ITextComponent itextcomponent = this.cachedComponents.get(l);
                int i2 = 0;
                for (final ITextComponent itextcomponent2 : itextcomponent) {
                    if (itextcomponent2 instanceof TextComponentString) {
                        i2 += this.mc.fontRendererObj.getStringWidth(((TextComponentString)itextcomponent2).getText());
                        if (i2 > i) {
                            return itextcomponent2;
                        }
                        continue;
                    }
                }
            }
            return null;
        }
        return null;
    }
    
    static class NextPageButton extends GuiButton
    {
        private final boolean isForward;
        
        public NextPageButton(final int p_i46316_1_, final int p_i46316_2_, final int p_i46316_3_, final boolean p_i46316_4_) {
            super(p_i46316_1_, p_i46316_2_, p_i46316_3_, 23, 13, "");
            this.isForward = p_i46316_4_;
        }
        
        @Override
        public void drawButton(final Minecraft p_191745_1_, final int p_191745_2_, final int p_191745_3_, final float p_191745_4_) {
            if (this.visible) {
                final boolean flag = p_191745_2_ >= this.xPosition && p_191745_3_ >= this.yPosition && p_191745_2_ < this.xPosition + this.width && p_191745_3_ < this.yPosition + this.height;
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                p_191745_1_.getTextureManager().bindTexture(GuiScreenBook.BOOK_GUI_TEXTURES);
                int i = 0;
                int j = 192;
                if (flag) {
                    i += 23;
                }
                if (!this.isForward) {
                    j += 13;
                }
                this.drawTexturedModalRect(this.xPosition, this.yPosition, i, j, 23, 13);
            }
        }
    }
}
