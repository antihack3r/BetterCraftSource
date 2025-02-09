/*
 * Decompiled with CFR 0.152.
 */
package org.spongepowered.asm.util.logging;

import javax.annotation.processing.Messager;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;
import org.spongepowered.asm.service.MixinService;

public final class MessageRouter {
    private static Messager messager;

    private MessageRouter() {
    }

    public static Messager getMessager() {
        if (messager == null) {
            messager = new LoggingMessager();
        }
        return messager;
    }

    public static void setMessager(Messager messager) {
        MessageRouter.messager = messager == null ? null : new DebugInterceptingMessager(messager);
    }

    static class DebugInterceptingMessager
    implements Messager {
        private final Messager wrapped;

        DebugInterceptingMessager(Messager messager) {
            this.wrapped = messager;
        }

        @Override
        public void printMessage(Diagnostic.Kind kind, CharSequence msg) {
            if (kind != Diagnostic.Kind.OTHER) {
                this.wrapped.printMessage(kind, msg);
            }
        }

        @Override
        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e2) {
            if (kind != Diagnostic.Kind.OTHER) {
                this.wrapped.printMessage(kind, msg, e2);
            }
        }

        @Override
        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e2, AnnotationMirror a2) {
            if (kind != Diagnostic.Kind.OTHER) {
                this.wrapped.printMessage(kind, msg, e2, a2);
            }
        }

        @Override
        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e2, AnnotationMirror a2, AnnotationValue v2) {
            if (kind != Diagnostic.Kind.OTHER) {
                this.wrapped.printMessage(kind, msg, e2, a2, v2);
            }
        }
    }

    static class LoggingMessager
    implements Messager {
        private static final ILogger logger = MixinService.getService().getLogger("mixin");

        LoggingMessager() {
        }

        @Override
        public void printMessage(Diagnostic.Kind kind, CharSequence msg) {
            logger.log(LoggingMessager.messageKindToLoggingLevel(kind), msg.toString(), new Object[0]);
        }

        @Override
        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e2) {
            logger.log(LoggingMessager.messageKindToLoggingLevel(kind), msg.toString(), new Object[0]);
        }

        @Override
        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e2, AnnotationMirror a2) {
            logger.log(LoggingMessager.messageKindToLoggingLevel(kind), msg.toString(), new Object[0]);
        }

        @Override
        public void printMessage(Diagnostic.Kind kind, CharSequence msg, Element e2, AnnotationMirror a2, AnnotationValue v2) {
            logger.log(LoggingMessager.messageKindToLoggingLevel(kind), msg.toString(), new Object[0]);
        }

        private static Level messageKindToLoggingLevel(Diagnostic.Kind kind) {
            switch (kind) {
                case ERROR: {
                    return Level.ERROR;
                }
                case WARNING: 
                case MANDATORY_WARNING: {
                    return Level.WARN;
                }
                case NOTE: {
                    return Level.INFO;
                }
            }
            return Level.DEBUG;
        }
    }
}

