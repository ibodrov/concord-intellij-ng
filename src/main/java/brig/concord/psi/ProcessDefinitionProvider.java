package brig.concord.psi;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.AstLoadingFilter;
import org.jetbrains.yaml.psi.YAMLDocument;

public class ProcessDefinitionProvider {

    private static final ProcessDefinitionProvider INSTANCE = new ProcessDefinitionProvider();

    public static ProcessDefinitionProvider getInstance() {
        return INSTANCE;
    }

    public ProcessDefinition get(PsiElement element) {
        return AstLoadingFilter.disallowTreeLoading(() -> _get(element));
    }

    private ProcessDefinition _get(PsiElement element) {
        YAMLDocument currentDoc = YamlPsiUtils.getDocument(element);
        if (currentDoc == null) {
            return null;
        }

        YAMLDocument rootDoc;

        VirtualFile rootFile = YamlPsiUtils.rootConcordYaml(element);
        if (rootFile != null) {
            PsiFile rootPsiFile = PsiManager.getInstance(element.getProject()).findFile(rootFile);
            rootDoc = YamlPsiUtils.getDocument(rootPsiFile);
        } else {
            rootDoc = currentDoc;
        }

        if (rootDoc == null || rootDoc.getContainingFile() == null || rootDoc.getContainingFile().getVirtualFile() == null) {
            return null;
        }

        return new ProcessDefinition(rootDoc);
    }
}
