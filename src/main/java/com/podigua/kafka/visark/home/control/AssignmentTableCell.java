package com.podigua.kafka.visark.home.control;

import com.podigua.kafka.visark.home.layout.ConsumerTopicPartitionPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tooltip;
import javafx.stage.PopupWindow;
import javafx.util.Duration;
import org.apache.kafka.clients.admin.MemberDescription;
import org.apache.kafka.common.TopicPartition;

import java.util.Comparator;
import java.util.stream.Collectors;

/**
 *
 **/
public class AssignmentTableCell extends TableCell<MemberDescription, Number> {
    private final Tooltip tooltip = new Tooltip();
    private final Label link = new Label();

    private ConsumerTopicPartitionPane pane = new ConsumerTopicPartitionPane();

    @Override
    protected void updateItem(Number item, boolean empty) {
        super.updateItem(item, empty);
        if (item == null | empty) {
            setGraphic(null);
            return;
        }
        TableRow<MemberDescription> row = getTableRow();
        pane.reset(row.getItem().assignment().topicPartitions().stream().sorted(Comparator.comparing(TopicPartition::topic).thenComparing(TopicPartition::partition)).collect(Collectors.toList()));
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setHideDelay(Duration.seconds(3));
        tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_RIGHT);
        link.setText(item + "");
        link.setTooltip(tooltip);
        link.setPrefWidth(80);
        tooltip.setGraphic(pane);
        setGraphic(link);
    }
}
