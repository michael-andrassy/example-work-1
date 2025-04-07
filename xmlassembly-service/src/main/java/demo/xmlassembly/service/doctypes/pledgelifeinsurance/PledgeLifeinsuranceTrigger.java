package demo.xmlassembly.service.doctypes.pledgelifeinsurance;


import demo.xmlassembly.service.datafetches.supplcollaterals.DFSupplementaryCollaterals;
import demo.xmlassembly.service.datafetches.supplcollaterals.LifeInsuranceCollateral;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import demo.xmlassembly.service.doctypes.DocumentTrigger;
import demo.xmlassembly.service.doctypes.EDocType;
import demo.xmlassembly.service.services.MultipleDocumentCreationTask;
import demo.xmlassembly.service.services.data.EBusinessContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/** A component that recognizes the need to create documents of a given type based on the request.
 *
 * For this document type the decision is also based on the results of service calls.
 *
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class PledgeLifeinsuranceTrigger implements DocumentTrigger {

    public final static String  PARAM_ADDRESSEE = "ADDRESSEE";
    public final static String  PARAM_POLICY_NO = "POLICY_NO";

    private final static List<EBusinessContext> applicableBusinessContexts = List.of(EBusinessContext.ContractReady);

    private final DFSupplementaryCollaterals dfSupplementaryCollaterals;

    @Override
    public List<DocumentCreationTask> getDocumentsToBeBuilt(MultipleDocumentCreationTask request) {

        if (applicableBusinessContexts.contains( request.getBusinessContext() ) ) {

            final Map<String, List<LifeInsuranceCollateral>> lifeinsurances4PolicyHolderName =
                    dfSupplementaryCollaterals.fetchLifeInsuranceCollaterals(request.getDossier());

            final List<DocumentCreationTask> documentCreationTasks =
                    lifeinsurances4PolicyHolderName.values().stream().
                    flatMap(List::stream).map(
                            I -> List.of(

                                    //for each insurance policy found we create two document creation tasks -> bank + client

                                    DocumentCreationTask.builder().
                                            docType(EDocType.PLEDGE_LIFE_INSURANCE).
                                            dossier(request.getDossier()).
                                            additionalParams(
                                                    Map.of(
                                                            PARAM_ADDRESSEE, "Copy for the Bank",
                                                            PARAM_POLICY_NO, I.getPolicyNo()
                                                    )
                                            ).
                                            build(),
                                    DocumentCreationTask.builder().
                                            docType(EDocType.PLEDGE_LIFE_INSURANCE).
                                            dossier(request.getDossier()).
                                            additionalParams(
                                                    Map.of(
                                                            PARAM_ADDRESSEE, "Copy for the Client",
                                                            PARAM_POLICY_NO, I.getPolicyNo()
                                                    )
                                            ).
                                            build()

                            ) //listOf
                    ). //map
                    flatMap(List::stream). //lets flatten those sublists per policy
                    toList();

            return documentCreationTasks;

        } else return List.of();
    } //m

} //class
