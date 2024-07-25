package org.addon.friza;

import org.parosproxy.paros.Constant;
import org.parosproxy.paros.network.HttpMessage;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.view.messagecontainer.http.HttpMessageContainer;
import org.zaproxy.zap.view.popup.PopupMenuItemHttpMessageContainer;

@SuppressWarnings("serial")
public class RightClickMsgMenu extends PopupMenuItemHttpMessageContainer {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private ExtensionFriza extension;

    public RightClickMsgMenu(ExtensionFriza ext, String label) {
        super(label);
       
        this.extension = ext;
    }

    @Override
    public void performAction(HttpMessage msg) {

        View.getSingleton()
                .showMessageDialog(
                        Constant.messages.getString(
                                ExtensionFriza.PREFIX + ".popup.msg",
                                msg.getRequestHeader().getURI().toString()));
    }

    @Override
    public boolean isEnableForInvoker(Invoker invoker, HttpMessageContainer httpMessageContainer) {
       
        return true;
    }

    @Override
    public boolean isSafe() {
      
        return true;
    }
}
