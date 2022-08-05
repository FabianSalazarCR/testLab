package net.bac.sbe.web.ebac.module.marchamobyconnectivity.action;

import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.bac.sbe.web.aps.library.logger.bean.BinnacleBean;
import net.bac.sbe.web.aps.library.logger.bean.LoggingDetailBean;
import net.bac.sbe.web.aps.library.logger.service.LoggerService;
import net.bac.sbe.web.common.config.bean.GlobalParametersBean;
import net.bac.sbe.web.common.ejb.marchamopayment.service.MarchamoByConnectivitySessionService;
import net.bac.sbe.web.common.exception.SessionFactoryException;
import net.bac.sbe.web.common.factory.SessionFrontEndFactory;
import net.bac.sbe.web.common.generalcodes.view.GeneralCodesView;
import net.bac.sbe.web.common.marchamopayment.bean.PlateInsuranceDataBean;
import net.bac.sbe.web.common.marchamopayment.exception.MarchamoPaymentException;
import net.bac.sbe.web.common.marchamopayment.marchamo.view.InsuranceProductListView;
import net.bac.sbe.web.common.marchamopayment.marchamoquery.view.PlateDetailRequestView;
import net.bac.sbe.web.common.marchamopayment.marchamoquery.view.PlateDetailView;
import net.bac.sbe.web.common.marchamopayment.marchamoquery.view.PlateListRequestView;
import net.bac.sbe.web.common.parametervalidator.SbeValidator;
import net.bac.sbe.web.ebac.common.action.EbacProductBaseAction;
import net.bac.sbe.web.ebac.common.config.bean.EbacLinksBean;
import net.bac.sbe.web.ebac.common.config.bean.EbacMenusBean;
import net.bac.sbe.web.ebac.common.config.bean.LinkOptionBean;
import net.bac.sbe.web.ebac.common.config.bean.MenuItemArrayBean;
import net.bac.sbe.web.ebac.common.helper.SessionRegistry;
import net.bac.sbe.web.ebac.common.view.UserSessionView;
import net.bac.sbe.web.ebac.module.marchamobyconnectivity.helper.MarchamoByConnectivityHelper;
import net.bac.sbe.web.ebac.module.marchamopayment.form.MarchamoQueryForm;
import net.bac.sbe.web.ebac.module.paguelo.helper.PagueloRegistry;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * <b>REVISI脫N 1.0</b><br>
 * <b>Fecha:</b> 19/07/2016<br>
 * <b>SP-CRI-19377-0001- Pago de marchamo</b><br>
 * <p>Se muestra la consulta de las placas digitadas</p>
 * 
 * <b>REVISI脫N 1.1</b><br>
 * <b>Fecha:</b> 15/08/2019<br>
 * <b>CRI-027399: Mejoras Marchamos de Bel y Movil </b><br>
 * <p>TICOM-marchamos</p>
 * 
 * 
 * <b>REVISI覰 1.2</b><br>
 * <b>Fecha:</b> 01/09/2021<br>
 * <b>PN-0002348 - CR-Mantenimiento y mejoras a Marchamos 2022</b><br>
 * <p>PAYTEAM-marchamos</p>
 * 
 * @author Centauro Solutions (1.0), lhernandezmo (1.1), Payteam(1.2)
 * @version 1.2
 */
public class MarchamoPlateQueryAction extends EbacProductBaseAction {
	/** C骴igo de respuesta consulta exitosa en INS */
	private static final String INS_SUCCESS_RESPONSE_CODE = "0"; //$NON-NLS-1$
	/** C骴igo que determina un seguro v醠ido*/
	private static final String SUCESS_ENSURANCE_CODE = "15"; //$NON-NLS-1$
	/*** listMessageMarchamo*/
	private static final String LIST_MARCHAMO_MESSAGE = "listMarchamo";//$NON-NLS-1$
	/** checkOferQuickPass */
	private static final String CHECK_OFER_QUICK_PASS = "checkOferQuickPass";//$NON-NLS-1$
	/** insuranceList */
	private static final String INSURANCE_LIST = "insuranceList";//$NON-NLS-1$
	/** plateSelected */
	private static final String PLATE_SELECTED = "plateSelected";//$NON-NLS-1$
	/** Key para la lista de tipos de placa */
	private static final String PLATE_TYPE = "plateType"; //$NON-NLS-1$
	/** Key para los numeros de placa */
	private static final String PLATE_NUMBER = "plateNumber"; //$NON-NLS-1$
	/** Constante para variable cero **/
	private static final String ZERO_VAR = "0"; //$NON-NLS-1$
	/** Lenguaje */
	private static final String LENGUAJE = "lenguaje";//$NON-NLS-1$
	/** Constante para la lista de marchamos**/
	private static final String MARCHAMO_LIST = "marchamoList"; //$NON-NLS-1$
	/** Identificador para obtener el key del par谩metro marchamoCountryCode */
	public static final String COUNTRY = "marchamoCountryCode"; //$NON-NLS-1$
	/** Identificador de la opcion Pago de Marchamos en el menu de Pagos */
	private static final String MENU_ITEM_ID = "MarchamoPaymentItem"; //$NON-NLS-1$
	/** Identificador del men煤 para la barra de navegaci贸n del pago de marchamos. */
	private static final String NAV_BAR_MENU_ID = "MarchamoPaymentNavBarMenu"; //$NON-NLS-1$
	/** Identificador del link para la opcion de consulta de placas. */
	private static final String MARCHAMO_PAYMENT_LINK = "MarchamoPaymentLink"; //$NON-NLS-1$
	/** String vacio **/
	private static final String EMPTY_STRING = ""; //$NON-NLS-1$
	/**
	separador
	 */
	private static final String HYPEN = "-"; //$NON-NLS-1$

	/**
	 * Error back link
	 * @since 1.0
	 */
	private static final String ERROR_BACK_LINK = "ERROR_BACK_LINK"; //$NON-NLS-1$

	/** EJB para la informaci贸n de modulo de Pago de Marchamos */
	private static final String MARCHAMO_BY_CONNECTIVITY_REF = "MarchamoByConnectivitySessionService"; //$NON-NLS-1$
	/** Identificador de view */
	private static final String MARCHAMO_PAYMENT_DATA_VIEW = "marchamoPaymentDataView"; //$NON-NLS-1$
	/**
	 * Metodo que ser谩 llamado para ejecutar la acci贸n
	 * @param mapping El conjunto de mapeos
	 * @param form El form al que este action referencia
	 * @param request El http servlet request
	 * @param response El http servlet response
	 * @return El pr贸ximo action a ser ejecutado, o el siguiente jsp a desplegar
	 */
	@Override
	public ActionForward executeEbacAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ActionForward forward = null;
		LinkOptionBean errorBackLink = null;
		LinkOptionBean backLink = null;
		SessionFrontEndFactory sessionFactory = null;
		UserSessionView userSessionInfo = null;
		MarchamoByConnectivitySessionService  marchamoByConnectivitySessionService= null;
		MarchamoQueryForm marchamoQueryForm = null;
		BinnacleBean binnacle = null;
		MenuItemArrayBean navBarMenu = null;
		PlateListRequestView plateList = null;
		PlateListRequestView plateListWithInsurance = null;

		String [] plateNumber = null;
		String [] plateType = null;
		TreeMap<String, Object> attributes = null;
		try {
			binnacle = new BinnacleBean(new GregorianCalendar(), getServerName(), LoggerService.LEVEL_INFO, GlobalParametersBean.getInstance().getValue(APPLICATION_NAME_KEY), 
					this.getClass().getName());
			
			forward = mapping.findForward(FAILURE_FORWARD);
			attributes = new TreeMap<>();
			// Se obtiene informacion del usuario
			if(request.getSession(false).getAttribute(SessionRegistry.USER_INFO) instanceof UserSessionView){
				userSessionInfo = (UserSessionView) request.getSession(false).getAttribute(SessionRegistry.USER_INFO);
				setLocale(request,userSessionInfo.getLanguage(),userSessionInfo.getCountry()); // parasoft-suppress CODSTA.READ.UATS "No aplica"

				// Se obtiene la barra de navegaci贸n
				navBarMenu = EbacMenusBean.getInstance().getMenus().getMenu(NAV_BAR_MENU_ID);
				// Se obtiene el link de retorno de pagina en caso de error
				errorBackLink = EbacLinksBean.getInstance().getLinkOption(MARCHAMO_PAYMENT_LINK);
				attributes.put(ERROR_BACK_LINK, errorBackLink);
				// Se obtiene el link de retorno de pagina
				backLink = EbacLinksBean.getInstance().getLinkOption(MARCHAMO_PAYMENT_LINK);


				// Instancia el servicio de pago de Marchamo
				sessionFactory = getSessionFrontEndFactory();
				marchamoByConnectivitySessionService = (MarchamoByConnectivitySessionService) sessionFactory.getSession(MARCHAMO_BY_CONNECTIVITY_REF);

				// Se obtiene el formulario
				marchamoQueryForm = (MarchamoQueryForm) form;
				plateNumber = marchamoQueryForm.getPlateNumber();
				plateType = marchamoQueryForm.getPlateType();
				Collection<PlateDetailView> plateListView = null;
				Collection<PlateDetailView> plateListViewWithInsurance = null;
				
				plateList = new PlateListRequestView();
				plateListWithInsurance = new PlateListRequestView();
				plateList.setCountry(userSessionInfo.getCountry());
				for(int numPlate = 0; numPlate < plateNumber.length; numPlate++){
					
					if(!SbeValidator.isBlankOrNull(plateNumber[numPlate])){
						PlateDetailRequestView plateDetailRequestView = new PlateDetailRequestView();
						PlateDetailRequestView plateDetailRequestViewWithInsurace = new PlateDetailRequestView();
						String plate =  plateNumber[numPlate];
						plate = plate.replaceAll(HYPEN, EMPTY_STRING);
						plateDetailRequestView.setPlateNumber(plate);
						plateDetailRequestView.setPlateType(plateType[numPlate]);
						
						plateDetailRequestViewWithInsurace.setPlateNumber(plate);
						plateDetailRequestViewWithInsurace.setPlateType(plateType[numPlate]);
						
						// Acci髇 encargada de trear los seguros(XML) por tipo de placa PART|CL|C|MOT para ser enviados en el request hacia el INS - Payteam PN-0002348
						plateDetailRequestViewWithInsurace.setInsuranceProductList(marchamoByConnectivitySessionService.getInsuranceConfigurate(plateType[numPlate]));
						plateList.addPlateRequestView(plateDetailRequestView);
						plateListWithInsurance.addPlateRequestView(plateDetailRequestViewWithInsurace);
					}
				}

				//Se llama al servicio de consulta
				plateListView = marchamoByConnectivitySessionService.getMarchamosQuery(plateList, userSessionInfo.getUserInfo().getIdentifier() , GlobalParametersBean.getInstance().getValue(PagueloRegistry.PAGUELO_CHANNEL_ID));
				plateListViewWithInsurance = marchamoByConnectivitySessionService.getMarchamosQuery(plateListWithInsurance, userSessionInfo.getUserInfo().getIdentifier() , GlobalParametersBean.getInstance().getValue(PagueloRegistry.PAGUELO_CHANNEL_ID));
				
				plateListView = mapPlateListWithInsuranceInfo(plateListView, plateListViewWithInsurance);
				
				marchamoQueryForm.setTotalAmount(ZERO_VAR);
				userSessionInfo.addSessionObject(MARCHAMO_LIST, plateListView);
				request.setAttribute(MARCHAMO_LIST, plateListView);
				
				// Instancia el servicio de mensaje de Marchamo
				final Collection<GeneralCodesView> listMarchamoMessage= getGeneralCodesbyCountry(sessionFactory,userSessionInfo.getLanguage());
				// Se asigna en el request los mensajes del footer de marchamos
				request.setAttribute(LIST_MARCHAMO_MESSAGE, listMarchamoMessage);
				
				// Se guarda en el request el link para la opcion de regreso a la pagina anterior.		
				request.setAttribute(MARCHAMO_PAYMENT_LINK, backLink);
				// Se guarda en el request la opcion seleccionada en el menu.
				request.setAttribute(SELECTED_OPTION_KEY, MENU_ITEM_ID);
				// Se guarda en el request la opcion seleccionada en el menu.
				request.setAttribute(LENGUAJE, userSessionInfo.getLanguage());
				// Se guarda en el request la barra de navegacion.
				request.setAttribute(NAV_BAR_MENU_KEY, navBarMenu);
				
				request.setAttribute(PLATE_NUMBER, plateNumber);
				request.setAttribute(PLATE_TYPE, plateType);
				
				request.setAttribute(PLATE_SELECTED,marchamoQueryForm.getPlateSelected());
				request.setAttribute(INSURANCE_LIST, marchamoQueryForm.getInsuranceList());
				request.setAttribute(CHECK_OFER_QUICK_PASS,marchamoQueryForm.getCheckOferQuickPass());
				//Limpia de la sesion los datos de pago
				request.getSession(false).setAttribute(MARCHAMO_PAYMENT_DATA_VIEW, null);
				forward = mapping.findForward(SUCCESS_FORWARD);
			}



			// Registra en bit谩cora.
			logBinnacle(request); // parasoft-suppress CODSTA.READ.UATS "No aplica"

		} catch (SessionFactoryException e) {
			MarchamoPaymentException mpe = new MarchamoPaymentException(e);
			LoggingDetailBean bean = new LoggingDetailBean(GlobalParametersBean.getInstance().getValue(APPLICATION_NAME_KEY), attributes, mpe, mpe.getMessage());
			binnacle.setErrorCode(mpe.getErrorCode());
			binnacle.setErrorInformation(mpe.getMessage());
			logBinnacleException(binnacle);
			logException(bean);

		} catch (MarchamoPaymentException mpe) {
			LoggingDetailBean bean = new LoggingDetailBean(GlobalParametersBean.getInstance().getValue(APPLICATION_NAME_KEY), attributes, mpe, mpe.getMessage());
			binnacle.setErrorCode(mpe.getErrorCode());
			binnacle.setErrorInformation(mpe.getMessage());
			logBinnacleException(binnacle);
			logException(bean);

		}


		return forward;
	}


	/**
	 * Metodo que se encarga de filtrar los seguros autoexpedibles 
	 * para ser agregados por el usuario
	 * @param plateListView
	 * @param plateListViewWithInsurance
	 * @return Collection<PlateDetailView>
	 */
	private static Collection<PlateDetailView> mapPlateListWithInsuranceInfo(
			Collection<PlateDetailView> plateListView,
			Collection<PlateDetailView> plateListViewWithInsurance) {
		
		Optional<PlateDetailView> insurance;
		
		for(PlateDetailView plateList : plateListView) {
			
			insurance = plateListViewWithInsurance.
			stream().
			filter(successCode->successCode.getStatus().equals(INS_SUCCESS_RESPONSE_CODE)).
			filter(x->x.getPlateUserData().getPlateNumber().equals(plateList.getPlateUserData().getPlateNumber())).
			findFirst();
			
			if(insurance.isPresent()) {
				PlateDetailView insurenceView = insurance.get();
				Collection<InsuranceProductListView> insuranceConfig =  insurenceView.getPlateUserData().getInsuranceConfigList();
				Collection<PlateInsuranceDataBean> insuranceDataBean =  insurenceView.getPlateUserData().getInsuranceDataBean();
				
				List<String> codeProductList = insuranceDataBean.
						stream().
						filter(x-> null != x.getCodRes()).
						filter(x->x.getCodRes().equals(SUCESS_ENSURANCE_CODE)).
						map(PlateInsuranceDataBean::getCodProduct).
						collect(Collectors.toList());
				
				Collection<InsuranceProductListView> insuranceConfigFilter = insuranceConfig.
						stream().
						filter(x->codeProductList.contains(x.getProductCode())).
						collect(Collectors.toList());
				
				plateList.getPlateUserData().setInsuranceConfigList(insuranceConfigFilter);
			}
			
		}
		
		return plateListView;
	}


	/**
	 * metodo para obtener los mensajes de marchamo del footer
	 * <!-- TICOM-marchamo -->
	 * @param sessionFactory 
	 * @param language 
	 * @return listMessageFooter
	 * @throws SessionFactoryException 
	 * @throws MarchamoPaymentException 
	 * @since 1.0
	 */
	protected  Collection<GeneralCodesView> getGeneralCodesbyCountry(SessionFrontEndFactory sessionFactory, String language) throws SessionFactoryException, MarchamoPaymentException {
		return MarchamoByConnectivityHelper.getGeneralCodesbyCountry(sessionFactory, language);
	}

	/**
	 * logueo de binnacle
	 * logBinnacle
	 * @param binnacle
	 * @since 1.0
	 */
	protected void logBinnacleException(BinnacleBean binnacle) {
		LoggerService.logBinnacle(binnacle);
	}

	/**
	 * logueo de exception
	 * logException
	 * @param bean
	 * @since 1.0
	 */
	protected void logException(LoggingDetailBean bean) {
		LoggerService.logException(LoggerService.LAYER_ACTION, LoggerService.LEVEL_ERROR, bean);
	}

	/**
	 * New del SessionFrontEndFactory
	 * getSessionFrontEndFactory
	 * @return SessionFrontEndFactory
	 * @since 1.0
	 */
	protected SessionFrontEndFactory getSessionFrontEndFactory() {
		return new SessionFrontEndFactory();
	}
}