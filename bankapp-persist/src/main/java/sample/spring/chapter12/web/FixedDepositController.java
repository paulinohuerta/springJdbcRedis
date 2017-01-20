package sample.spring.chapter12.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ModelAttribute;
import sample.spring.chapter12.domain.FixedDepositDetails;
import sample.spring.chapter12.service.FixedDepositService;

@Controller
@RequestMapping(path = "/fixedDeposit")
public class FixedDepositController {

	@Autowired
	private FixedDepositService fixedDepositService;

	@RequestMapping("/list")
	public String listFixedDeposits(Model model) {
                model.addAttribute("fdList",fixedDepositService.getFixedDeposits());
		return "fixedDepositList";
	}
/*
        @ModelAttribute("obj")
        public FixedDepositDetails defaultUser() {
                FixedDepositDetails fixedCuenta = new FixedDepositDetails();
		fixedCuenta.setEmail("Introduce email válido");
                return fixedCuenta;
        }
*/

	@RequestMapping(params = "fdAction=createFDForm", method = RequestMethod.POST)
	public String showOpenFixedDepositForm(Model model) {
                FixedDepositDetails fixedCuenta = new FixedDepositDetails();
		fixedCuenta.setEmail("Introduce email valid");
                model.addAttribute("obj",fixedCuenta);
		return "createFixedDepositForm1";
	}
	@RequestMapping(params = "fdAction=create", method = RequestMethod.POST)
	public String openFixedDeposit(@ModelAttribute("obj") FixedDepositDetails ob,Model model) {
		String depositAmount = ob.getDepositAmount();
		String tenure = ob.getTenure();
		String email = ob.getEmail();

		Map<String, Object> modelData = new HashMap<String, Object>();

		if (!NumberUtils.isNumber(depositAmount)) {
			modelData.put("error.depositAmount", "enter a valid number");
		} else if (NumberUtils.toInt(depositAmount) < 1000) {
			modelData.put("error.depositAmount",
					"must be greater than or equal to 1000");
		}

		if (!NumberUtils.isNumber(tenure)) {
			modelData.put("error.tenure", "enter a valid number");
		} else if (NumberUtils.toInt(tenure) < 12) {
			modelData.put("error.tenure", "must be greater than or equal to 12");
		}

		if (email == null || "".equalsIgnoreCase(email)) {
			modelData.put("error.email", "must not be blank");
		} else if (!email.contains("@")) {
			modelData.put("error.email", "not a well-formed email address");
		}

		if (modelData.size() > 0) { // --this means there are validation errors
                        model.addAttribute("modelData",modelData);
			return "createFixedDepositForm1";
		} else {
			fixedDepositService.saveFixedDeposit(ob);
			return "redirect:/fixedDeposit/list";
		}
	}

	@RequestMapping(params = "fdAction=edit", method = RequestMethod.POST)
	public ModelAndView editFixedDeposit(
			@RequestParam MultiValueMap<String, String> params) {

		String depositAmount = params.get("depositAmount").get(0);
		String tenure = params.get("tenure").get(0);
		String email = params.get("email").get(0);
		String id = params.get("id").get(0);

		Map<String, Object> modelData = new HashMap<String, Object>();

		if (!NumberUtils.isNumber(depositAmount)) {
			modelData.put("error.depositAmount", "enter a valid number");
		} else if (NumberUtils.toInt(depositAmount) < 1000) {
			modelData.put("error.depositAmount",
					"must be greater than or equal to 1000");
		}

		if (!NumberUtils.isNumber(tenure)) {
			modelData.put("error.tenure", "enter a valid number");
		} else if (NumberUtils.toInt(tenure) < 12) {
			modelData
					.put("error.tenure", "must be greater than or equal to 12");
		}

		if (email == null || "".equalsIgnoreCase(email)) {
			modelData.put("error.email", "must not be blank");
		} else if (!email.contains("@")) {
			modelData.put("error.email", "not a well-formed email address");
		}

		FixedDepositDetails fixedDepositDetails = new FixedDepositDetails();
		fixedDepositDetails.setId(Integer.parseInt(id));
		fixedDepositDetails.setDepositAmount(depositAmount);
		fixedDepositDetails.setEmail(email);
		fixedDepositDetails.setTenure(tenure);

		if (modelData.size() > 0) { // --this means there are validation errors
			modelData.put("fixedDepositDetails", fixedDepositDetails);
			return new ModelAndView("editFixedDepositForm", modelData);
		} else {
			fixedDepositService.editFixedDeposit(fixedDepositDetails);
			return new ModelAndView("redirect:/fixedDeposit/list");
		}
	}

	@RequestMapping(params = "fdAction=close", method = RequestMethod.GET)
	public String closeFixedDeposit(
			@RequestParam(name = "fixedDepositId") int fdId) {
		fixedDepositService.closeFixedDeposit(fdId);
		return "redirect:/fixedDeposit/list";
	}

	@RequestMapping(params = "fdAction=view", method = RequestMethod.GET)
	public ModelAndView viewFixedDepositDetails(HttpServletRequest request) {
		FixedDepositDetails fixedDepositDetails = fixedDepositService
				.getFixedDeposit(Integer.parseInt(request
						.getParameter("fixedDepositId")));
		ModelMap modelMap = new ModelMap();
		modelMap.addAttribute(fixedDepositDetails);
		return new ModelAndView("editFixedDepositForm", modelMap);
	}

	@ExceptionHandler
	public String handleException(Exception ex) {
		return "error";
	}

}
