package org.zanata.rest.editor.service;

import com.google.common.collect.Lists;
import net.customware.gwt.dispatch.shared.ActionException;
import org.omg.PortableInterceptor.ACTIVE;
import org.zanata.common.EntityStatus;
import org.zanata.common.LocaleId;
import org.zanata.dao.ReviewCriteriaDAO;
import org.zanata.dao.TextFlowTargetDAO;
import org.zanata.dao.TextFlowTargetReviewCommentsDAO;
import org.zanata.model.HAccount;
import org.zanata.model.HLocale;
import org.zanata.model.HProject;
import org.zanata.model.HProjectIteration;
import org.zanata.model.HTextFlowTarget;
import org.zanata.model.HTextFlowTargetReviewComment;
import org.zanata.model.ReviewCriteria;
import org.zanata.rest.editor.dto.ReviewData;
import org.zanata.rest.editor.service.resource.TranslationReviewResource;
import org.zanata.security.ZanataIdentity;
import org.zanata.security.annotations.Authenticated;
import org.zanata.service.LocaleService;
import org.zanata.webtrans.shared.model.ReviewCriterionId;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.List;


@Path(TranslationReviewResource.SERVICE_PATH)
@RequestScoped
public class TranslationReviewService implements TranslationReviewResource {
    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(TranslationReviewService.class);

    @Inject
    private TextFlowTargetDAO textFlowTargetDAO;
    @Inject
    private LocaleService localeServiceImpl;
    @Inject
    private ZanataIdentity identity;
    @Inject
    private ReviewCriteriaDAO reviewCriteriaDAO;
    @Inject
    @Authenticated
    private HAccount authenticatedAccount;
    @Inject
    private TextFlowTargetReviewCommentsDAO textFlowTargetReviewCommentsDAO;

    @Override
    public Response put(String localeId, ReviewData data) {
        HLocale locale =
                localeServiceImpl.getByLocaleId(localeId);
        if (locale == null) {
            return Response.status(
                    Response.Status.BAD_REQUEST)
                    .entity(Lists.newArrayList("locale ID is invalid, or not found."))
                    .build();
        }
        HTextFlowTarget hTextFlowTarget = textFlowTargetDAO.getTextFlowTarget(
                data.getTransUnitId(), new LocaleId(localeId));
        if (hTextFlowTarget == null
                || hTextFlowTarget.getState().isUntranslated()) {
             return Response.status(
                     Response.Status.BAD_REQUEST)
                     .entity(Lists.newArrayList("comment on untranslated message is pointless!"))
                     .build();
        }
        HProjectIteration projectIteration =
                hTextFlowTarget.getTextFlow().getDocument()
                        .getProjectIteration();
        HProject project = projectIteration.getProject();
        if (project.getStatus() != EntityStatus.ACTIVE || projectIteration.getStatus()
                != EntityStatus.ACTIVE) {
            return Response.status(
                    Response.Status.FORBIDDEN)
                    .entity(Lists.newArrayList("project or version is not active."))
                    .build();
        }
        identity.checkPermission("review-comment", locale, project);

        HTextFlowTargetReviewComment hComment;
        ReviewCriterionId reviewId = new ReviewCriterionId(data.getReviewCriteriaId());
        ReviewCriteria reviewCriteria = reviewCriteriaDAO.findById(reviewId.getId());
        if (reviewCriteria == null) {
            return Response.status(
                    Response.Status.BAD_REQUEST)
                    .entity(Lists.newArrayList("invalid review criteria."))
                    .build();
        }
        hComment = hTextFlowTarget.addReview(authenticatedAccount.getPerson(), reviewCriteria, data.getComment());
        textFlowTargetReviewCommentsDAO.makePersistent(hComment);
        textFlowTargetReviewCommentsDAO.flush();

        log.info("success");
        return Response.ok(data).build();
    }
}
