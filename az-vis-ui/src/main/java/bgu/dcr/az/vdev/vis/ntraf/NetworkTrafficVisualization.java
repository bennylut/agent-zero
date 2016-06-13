/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.dcr.az.vdev.vis.ntraf;

import bgu.dcr.az.vdev.timelines.MessageUsageChange;
import bgu.dcr.az.vdev.timelines.MessagesInQueueChange;
import bgu.dcr.az.vdev.timelines.MessagesOnLineChange;
import bgu.dcr.az.vdev.timelines.CurrentMessageHandlingChange;
import bgu.dcr.az.api.Agent;
import bgu.dcr.az.api.Hooks;
import bgu.dcr.az.api.Message;
import bgu.dcr.az.api.ano.Register;
import bgu.dcr.az.api.exen.Execution;
import bgu.dcr.az.api.exen.mdef.Visualization;
import bgu.dcr.az.api.exen.mdef.VisualizationDrawer;
import bgu.dcr.az.api.exen.vis.VisualizationFrameBuffer;
import bgu.dcr.az.vdev.vis.misc.AgentIcon;
import bgu.dcr.az.vdev.vis.ngr.NetworkGraphVisualizationDrawer;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import resources.img.ResourcesImgAnchor;

/**
 *
 * @author Administrator
 */
@Register(name = "net-traffic-vis")
public class NetworkTrafficVisualization implements Visualization {

    private NetworkTrafficVisualizationDrawer view = null;
    private Execution ex;
    private VisualizationFrameBuffer buffer;

    @Override
    public void install(Execution ex, final VisualizationFrameBuffer buffer) {
        this.ex = ex;
        this.buffer = buffer;
        final int numberOfAgents = ex.getAgents().length;

//        new Hooks.BeforeMessageSentHook() {
//            @Override
//            public void hook(int senderId, int recepientId, Message msg) {
//                final MessagesInQueueChange change = new MessagesInQueueChange(numberOfAgents, recepientId, 1);
//                buffer.submitChange(MessagesInQueueChange.ID, senderId, change);
//            }
//        }.hookInto(ex);
//
//        new Hooks.BeforeMessageProcessingHook() {
//            @Override
//            public void hook(Agent a, Message msg) {
//                buffer.submitChange(MessagesInQueueChange.ID, a.getId(), new MessagesInQueueChange(numberOfAgents, a.getId(), -1));
//            }
//        }.hookInto(ex);

//        MessagesInQueueChange.store(buffer, numberOfAgents);
        MessagesOnLineChange.install(ex, buffer);
        CurrentMessageHandlingChange.install(ex, buffer, 1);
        MessageUsageChange.install(ex, buffer);
    }

    @Override
    public VisualizationDrawer loadOrRetreiveView(Object canvas) {
        if (view == null) {
            view = new NetworkTrafficVisualizationDrawer();
            view.init((Pane) canvas, ex, buffer);
        }

        return view;
    }

    @Override
    public Image getThumbnail() {
        return new Image(ResourcesImgAnchor.class.getResourceAsStream("camera.png"));
    }

    @Override
    public String getDescriptionURL() {
        return getClass().getResource("___desc.html").toExternalForm();
    }
}
