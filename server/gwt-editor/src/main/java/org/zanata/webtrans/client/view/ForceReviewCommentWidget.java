/*
 * Copyright 2013, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */

package org.zanata.webtrans.client.view;

import java.util.List;

import org.zanata.webtrans.client.keys.ShortcutContext;
import org.zanata.webtrans.client.presenter.KeyShortcutPresenter;
import org.zanata.webtrans.client.resources.WebTransMessages;
import org.zanata.webtrans.client.ui.DialogBoxCloseButton;
import org.zanata.webtrans.client.ui.ReviewCommentInputWidget;
import org.zanata.webtrans.client.ui.ShortcutContextAwareDialogBox;
import org.zanata.webtrans.shared.model.ReviewCriterionId;
import org.zanata.webtrans.shared.rest.dto.TransReviewCriteria;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Patrick Huang <a
 *         href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Singleton
public class ForceReviewCommentWidget extends ShortcutContextAwareDialogBox
        implements ForceReviewCommentDisplay {
    private final ReviewCommentInputWidget inputWidget;
    private final FlowPanel panel;
    private final ListBox listBox;

    @Inject
    public ForceReviewCommentWidget(WebTransMessages messages,
            KeyShortcutPresenter keyShortcutPresenter) {
        super(false, true, ShortcutContext.RejectConfirmationPopup,
                keyShortcutPresenter);
        setGlassEnabled(true);

        setText(messages.rejectCommentTitle());

        inputWidget = new ReviewCommentInputWidget();
        inputWidget.setButtonText(messages.confirmRejection());
        panel = new FlowPanel();
        panel.setStyleName("new-zanata");
        panel.setWidth("800px");
        listBox = new ListBox();
        listBox.setMultipleSelect(false);
        // add an empty item
        listBox.addItem("--- Select a predefined criteria ---");
        panel.add(listBox);
        panel.add(inputWidget);
        DialogBoxCloseButton button = new DialogBoxCloseButton(this);
        button.setText(messages.cancel());
        panel.add(button);
        setWidget(panel);
    }

    @Override
    public void setListener(Listener listener) {
        inputWidget.setListener(listener);
    }

    @Override
    public void clearInput() {
        inputWidget.setText("");
    }

    @Override
    public String getComment() {
        return inputWidget.getText();
    }

    @Override
    public void setReviewCriteria(
            Listener listener,
            List<TransReviewCriteria> reviewCriteria) {
        reviewCriteria.forEach(r -> listBox.addItem(r.getPriority() + " - " + r.getDescription()));
        // if admin has not define review criteria, we don't show the selection box
        listBox.setVisible(reviewCriteria.size() > 0);
        listBox.addChangeHandler(event -> {
            int selectedIndex = listBox.getSelectedIndex();
            // first item is the empty description line
            if (selectedIndex != 0) {
                TransReviewCriteria selected =
                        reviewCriteria.get(selectedIndex - 1);
                listener.selectReviewCriteria(new ReviewCriterionId(selected.getId()));
            }
        });

    }
}
