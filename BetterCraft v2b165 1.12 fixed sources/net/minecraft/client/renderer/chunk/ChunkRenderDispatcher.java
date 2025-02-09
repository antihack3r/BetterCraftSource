// 
// Decompiled by Procyon v0.6.0
// 

package net.minecraft.client.renderer.chunk;

import com.google.common.primitives.Doubles;
import java.util.Iterator;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.Futures;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.Minecraft;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.BlockRenderLayer;
import java.util.Collection;
import net.minecraft.util.math.MathHelper;
import com.google.common.collect.Queues;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import java.util.Queue;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.apache.logging.log4j.Logger;

public class ChunkRenderDispatcher
{
    private static final Logger LOGGER;
    private static final ThreadFactory THREAD_FACTORY;
    private final int countRenderBuilders;
    private final List<Thread> listWorkerThreads;
    private final List<ChunkRenderWorker> listThreadedWorkers;
    private final PriorityBlockingQueue<ChunkCompileTaskGenerator> queueChunkUpdates;
    private final BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders;
    private final WorldVertexBufferUploader worldVertexUploader;
    private final VertexBufferUploader vertexUploader;
    private final Queue<PendingUpload> queueChunkUploads;
    private final ChunkRenderWorker renderWorker;
    
    static {
        LOGGER = LogManager.getLogger();
        THREAD_FACTORY = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).build();
    }
    
    public ChunkRenderDispatcher() {
        this(-1);
    }
    
    public ChunkRenderDispatcher(final int p_i7_1_) {
        this.listWorkerThreads = (List<Thread>)Lists.newArrayList();
        this.listThreadedWorkers = (List<ChunkRenderWorker>)Lists.newArrayList();
        this.queueChunkUpdates = Queues.newPriorityBlockingQueue();
        this.worldVertexUploader = new WorldVertexBufferUploader();
        this.vertexUploader = new VertexBufferUploader();
        this.queueChunkUploads = (Queue<PendingUpload>)Queues.newPriorityQueue();
        final int i = Math.max(1, (int)(Runtime.getRuntime().maxMemory() * 0.3) / 10485760);
        final int j = Math.max(1, MathHelper.clamp(Runtime.getRuntime().availableProcessors(), 1, i / 5));
        if (p_i7_1_ < 0) {
            this.countRenderBuilders = MathHelper.clamp(j, 1, i);
        }
        else {
            this.countRenderBuilders = p_i7_1_;
        }
        if (j > 1) {
            for (int k = 0; k < j; ++k) {
                final ChunkRenderWorker chunkrenderworker = new ChunkRenderWorker(this);
                final Thread thread = ChunkRenderDispatcher.THREAD_FACTORY.newThread(chunkrenderworker);
                thread.start();
                this.listThreadedWorkers.add(chunkrenderworker);
                this.listWorkerThreads.add(thread);
            }
        }
        this.queueFreeRenderBuilders = (BlockingQueue<RegionRenderCacheBuilder>)Queues.newArrayBlockingQueue(this.countRenderBuilders);
        for (int l = 0; l < this.countRenderBuilders; ++l) {
            this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
        }
        this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
    }
    
    public String getDebugInfo() {
        return this.listWorkerThreads.isEmpty() ? String.format("pC: %03d, single-threaded", this.queueChunkUpdates.size()) : String.format("pC: %03d, pU: %1d, aB: %1d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
    }
    
    public boolean runChunkUploads(final long p_178516_1_) {
        boolean flag = false;
        boolean flag2;
        do {
            flag2 = false;
            if (this.listWorkerThreads.isEmpty()) {
                final ChunkCompileTaskGenerator chunkcompiletaskgenerator = this.queueChunkUpdates.poll();
                if (chunkcompiletaskgenerator != null) {
                    try {
                        this.renderWorker.processTask(chunkcompiletaskgenerator);
                        flag2 = true;
                    }
                    catch (final InterruptedException var8) {
                        ChunkRenderDispatcher.LOGGER.warn("Skipped task due to interrupt");
                    }
                }
            }
            synchronized (this.queueChunkUploads) {
                if (!this.queueChunkUploads.isEmpty()) {
                    this.queueChunkUploads.poll().uploadTask.run();
                    flag2 = true;
                    flag = true;
                }
                monitorexit(this.queueChunkUploads);
            }
        } while (p_178516_1_ != 0L && flag2 && p_178516_1_ >= System.nanoTime());
        return flag;
    }
    
    public boolean updateChunkLater(final RenderChunk chunkRenderer) {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag2;
        try {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskChunk();
            chunkcompiletaskgenerator.addFinishRunnable(new Runnable() {
                @Override
                public void run() {
                    ChunkRenderDispatcher.this.queueChunkUpdates.remove(chunkcompiletaskgenerator);
                }
            });
            final boolean flag1 = this.queueChunkUpdates.offer(chunkcompiletaskgenerator);
            if (!flag1) {
                chunkcompiletaskgenerator.finish();
            }
            flag2 = flag1;
        }
        finally {
            chunkRenderer.getLockCompileTask().unlock();
        }
        chunkRenderer.getLockCompileTask().unlock();
        return flag2;
    }
    
    public boolean updateChunkNow(final RenderChunk chunkRenderer) {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag;
        try {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskChunk();
            try {
                this.renderWorker.processTask(chunkcompiletaskgenerator);
            }
            catch (final InterruptedException ex) {}
            flag = true;
        }
        finally {
            chunkRenderer.getLockCompileTask().unlock();
        }
        chunkRenderer.getLockCompileTask().unlock();
        return flag;
    }
    
    public void stopChunkUpdates() {
        this.clearChunkUpdates();
        final List<RegionRenderCacheBuilder> list = (List<RegionRenderCacheBuilder>)Lists.newArrayList();
        while (list.size() != this.countRenderBuilders) {
            this.runChunkUploads(Long.MAX_VALUE);
            try {
                list.add(this.allocateRenderBuilder());
            }
            catch (final InterruptedException ex) {}
        }
        this.queueFreeRenderBuilders.addAll((Collection<?>)list);
    }
    
    public void freeRenderBuilder(final RegionRenderCacheBuilder p_178512_1_) {
        this.queueFreeRenderBuilders.add(p_178512_1_);
    }
    
    public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
        return this.queueFreeRenderBuilders.take();
    }
    
    public ChunkCompileTaskGenerator getNextChunkUpdate() throws InterruptedException {
        return this.queueChunkUpdates.take();
    }
    
    public boolean updateTransparencyLater(final RenderChunk chunkRenderer) {
        chunkRenderer.getLockCompileTask().lock();
        boolean flag4;
        try {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = chunkRenderer.makeCompileTaskTransparency();
            if (chunkcompiletaskgenerator != null) {
                chunkcompiletaskgenerator.addFinishRunnable(new Runnable() {
                    @Override
                    public void run() {
                        ChunkRenderDispatcher.this.queueChunkUpdates.remove(chunkcompiletaskgenerator);
                    }
                });
                final boolean flag2 = this.queueChunkUpdates.offer(chunkcompiletaskgenerator);
                return flag2;
            }
            final boolean flag3 = flag4 = true;
        }
        finally {
            chunkRenderer.getLockCompileTask().unlock();
        }
        chunkRenderer.getLockCompileTask().unlock();
        return flag4;
    }
    
    public ListenableFuture<Object> uploadChunk(final BlockRenderLayer p_188245_1_, final BufferBuilder p_188245_2_, final RenderChunk p_188245_3_, final CompiledChunk p_188245_4_, final double p_188245_5_) {
        if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
            if (OpenGlHelper.useVbo()) {
                this.uploadVertexBuffer(p_188245_2_, p_188245_3_.getVertexBufferByLayer(p_188245_1_.ordinal()));
            }
            else {
                this.uploadDisplayList(p_188245_2_, ((ListedRenderChunk)p_188245_3_).getDisplayList(p_188245_1_, p_188245_4_), p_188245_3_);
            }
            p_188245_2_.setTranslation(0.0, 0.0, 0.0);
            return Futures.immediateFuture((Object)null);
        }
        final ListenableFutureTask<Object> listenablefuturetask = ListenableFutureTask.create(new Runnable() {
            @Override
            public void run() {
                ChunkRenderDispatcher.this.uploadChunk(p_188245_1_, p_188245_2_, p_188245_3_, p_188245_4_, p_188245_5_);
            }
        }, (Object)null);
        synchronized (this.queueChunkUploads) {
            this.queueChunkUploads.add(new PendingUpload(listenablefuturetask, p_188245_5_));
            final ListenableFutureTask<Object> listenableFutureTask = listenablefuturetask;
            monitorexit(this.queueChunkUploads);
            return listenableFutureTask;
        }
    }
    
    private void uploadDisplayList(final BufferBuilder p_178510_1_, final int p_178510_2_, final RenderChunk chunkRenderer) {
        GlStateManager.glNewList(p_178510_2_, 4864);
        GlStateManager.pushMatrix();
        chunkRenderer.multModelviewMatrix();
        this.worldVertexUploader.draw(p_178510_1_);
        GlStateManager.popMatrix();
        GlStateManager.glEndList();
    }
    
    private void uploadVertexBuffer(final BufferBuilder p_178506_1_, final VertexBuffer vertexBufferIn) {
        this.vertexUploader.setVertexBuffer(vertexBufferIn);
        this.vertexUploader.draw(p_178506_1_);
    }
    
    public void clearChunkUpdates() {
        while (!this.queueChunkUpdates.isEmpty()) {
            final ChunkCompileTaskGenerator chunkcompiletaskgenerator = this.queueChunkUpdates.poll();
            if (chunkcompiletaskgenerator != null) {
                chunkcompiletaskgenerator.finish();
            }
        }
    }
    
    public boolean hasChunkUpdates() {
        return this.queueChunkUpdates.isEmpty() && this.queueChunkUploads.isEmpty();
    }
    
    public void stopWorkerThreads() {
        this.clearChunkUpdates();
        for (final ChunkRenderWorker chunkrenderworker : this.listThreadedWorkers) {
            chunkrenderworker.notifyToStop();
        }
        for (final Thread thread : this.listWorkerThreads) {
            try {
                thread.interrupt();
                thread.join();
            }
            catch (final InterruptedException interruptedexception) {
                ChunkRenderDispatcher.LOGGER.warn("Interrupted whilst waiting for worker to die", interruptedexception);
            }
        }
        this.queueFreeRenderBuilders.clear();
    }
    
    public boolean hasNoFreeRenderBuilders() {
        return this.queueFreeRenderBuilders.isEmpty();
    }
    
    class PendingUpload implements Comparable<PendingUpload>
    {
        private final ListenableFutureTask<Object> uploadTask;
        private final double distanceSq;
        
        public PendingUpload(final ListenableFutureTask<Object> p_i46994_2_, final double p_i46994_3_) {
            this.uploadTask = p_i46994_2_;
            this.distanceSq = p_i46994_3_;
        }
        
        @Override
        public int compareTo(final PendingUpload p_compareTo_1_) {
            return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
        }
    }
}
