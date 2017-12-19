package net.andreho.dyn.classpath;

import org.assertj.core.util.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.Callable;

import static net.andreho.dyn.classpath.DynamicClassPath.defaultManager;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * <br/>Created by a.hofmann on 19.12.2017 at 19:42.
 */
class InstallationManagerTest
  implements Opcodes {

  private static final URL TEMP_FOLDER;
  private static final Logger LOG = LoggerFactory.getLogger(InstallationManagerTest.class);

  static {
    try {
      Path generated = Files.temporaryFolder().toPath().resolve("generated");
      generated.toFile().mkdirs();
      TEMP_FOLDER = generated.toUri().toURL();

      LOG.debug("Target folder: {}", TEMP_FOLDER);
    } catch (MalformedURLException e) {
      throw new IllegalStateException(e);
    }
  }

  public static byte[] dump()
  throws Exception {
    MethodVisitor mv;
    ClassWriter cw = new ClassWriter(0);
    cw.visit(52, ACC_SUPER, "net/andreho/dyn/classpath/ATask",
             "Ljava/lang/Object;Ljava/util/concurrent/Callable<Ljava/lang/String;>;", "java/lang/Object",
             new String[]{"java/util/concurrent/Callable"});

    cw.visitSource("InstallationManagerTest.java", null);

    {
      mv = cw.visitMethod(0, "<init>", "()V", null, null);
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(30, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
      mv.visitInsn(RETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "Lnet/andreho/dyn/classpath/ATask;", null, l0, l1, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC, "call", "()Ljava/lang/String;", null, new String[]{"java/lang/Exception"});
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(33, l0);
      mv.visitLdcInsn("OK");
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "Lnet/andreho/dyn/classpath/ATask;", null, l0, l1, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    {
      mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "call", "()Ljava/lang/Object;", null,
                          new String[]{"java/lang/Exception"});
      mv.visitCode();
      Label l0 = new Label();
      mv.visitLabel(l0);
      mv.visitLineNumber(30, l0);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKEVIRTUAL, "net/andreho/dyn/classpath/ATask", "call", "()Ljava/lang/String;", false);
      mv.visitInsn(ARETURN);
      Label l1 = new Label();
      mv.visitLabel(l1);
      mv.visitLocalVariable("this", "Lnet/andreho/dyn/classpath/ATask;", null, l0, l1, 0);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
    }
    cw.visitEnd();
    return cw.toByteArray();
  }

  @Test
  @DisplayName("Installation on a generated class must be possible")
  void installGenerateClass()
  throws Exception {
    InstallationManager.Result result =
      defaultManager().install("generated",
                               getClass().getClassLoader(),
                               TEMP_FOLDER);

    assertEquals(InstallationManager.Outcome.SUCCESSFUL, result.getOutcome());
    assertTrue(result.getEntry().isPresent());
    Entry entry = result.getEntry().get();
    entry.add(dump(), "net/andreho/dyn/classpath/ATask.class");

    Class<? extends Callable<String>> cls =
      (Class<? extends Callable<String>>) Class.forName("net.andreho.dyn.classpath.ATask");
    Callable<String> callable = cls.newInstance();
    assertEquals("OK", callable.call());
  }
}
//class ATask implements Callable<String> {
//  @Override
//  public String call() throws Exception {
//    return "OK";
//  }
//}