package demo.xmlassembly.service.doctypes.pledgelifeinsurance;

import demo.xmlassembly.service.datafetches.shared.ESigneeRole;
import demo.xmlassembly.service.datafetches.shared.Signee;
import demo.xmlassembly.service.datafetches.supplcollaterals.LifeInsuranceCollateral;
import demo.xmlassembly.service.doctypes.DocumentCreationTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Implements all business logic for the given document type.
 *
 */
@Slf4j
@Component
public class PledgeLifeinsuranceDataConsolidation {

    public PledgeLifeinsuranceConsolidatedData consolidate(DocumentCreationTask documentCreationTask, PledgeLifeinsuranceFetchedData fetchedData) {

        //of course, if that was real this param value would lead us to some enum value which will be translated to text later
        final String addresse = documentCreationTask.getAdditionalParams().get(PledgeLifeinsuranceTrigger.PARAM_ADDRESSEE);
        if (addresse == null || addresse.isBlank()) {
            throw new IllegalStateException("Addressee must not be blank or empty");
        }

        //extract the selected policy-number from those additional params
        final String selectedPolicyNo =
                documentCreationTask.getAdditionalParams().entrySet().stream().
                filter(E -> E.getKey().equals(PledgeLifeinsuranceTrigger.PARAM_POLICY_NO)).
                map(Map.Entry::getValue).findFirst().
                orElseThrow(() -> new RuntimeException("Must not happen - didn't find policyNo param"));

        // this one is a bit cumbersome and a strong indicator that the data-fetch interface is less than ideal - never mind,
        // this example is about the flexibility this approach offers (trigger - fetch - consolidate - xml)
        final LifeInsuranceCollateral matchingInsurancePolicy = fetchedData.getMapPolicyHolderName2LifeInsurances().values().stream().flatMap(List::stream).
                filter(I -> I.getPolicyNo().equals(selectedPolicyNo)).findFirst().
                orElseThrow(() -> new RuntimeException("Must not happen - didn't find Lifeinsurance for policyNo " + selectedPolicyNo));

        //specified business logic : borrowers and spouses and security givers have to sign

        final Signee insurancePolicyHolderSignee = Signee.builder().
                fullname( matchingInsurancePolicy.getPolicyholderFullname() ).
                role( ESigneeRole.SupplementaryCollateralContributor ).
                build();

        List<Signee> allSignees = new ArrayList<>();
        allSignees.add( insurancePolicyHolderSignee );
        allSignees.addAll( fetchedData.getBorrowerData().getPotentialSignees() );

        // --------------------------------------------------------------------------

        final PledgeLifeinsuranceConsolidatedData result =
                PledgeLifeinsuranceConsolidatedData.builder().
                        documentCreationTask(documentCreationTask).
                        addressee( addresse ).
                        borrowerData( fetchedData.getBorrowerData() ).
                        matchingLifeInsuranceCollateral( matchingInsurancePolicy ).
                        allSignees(Collections.unmodifiableList( allSignees ) ).
                        build();

        return result;
    } //m

} //class