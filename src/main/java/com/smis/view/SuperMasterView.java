package com.smis.view;

import com.smis.dbservice.Dbservice;
import com.smis.entity.District;
import com.smis.entity.Impldistrict;
import com.smis.entity.State;
import com.smis.entity.Users;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;

@PageTitle("States || Districts")
@Route(value="master", layout=MainLayout.class)
@RolesAllowed("SUPER")
public class SuperMasterView extends VerticalLayout {
	
	Dbservice service;
	Grid<District> dgrid=new Grid<>(District.class);
	Grid<Impldistrict> idgrid=new Grid<>(Impldistrict.class);
	Grid<State> sgrid=new Grid<>(State.class);
	Grid<Users> ugrid=new Grid<>(Users.class);
	StateForm stateform;
	DistrictForm distform;
	ImpldistrictForm impldistform;
	public SuperMasterView(Dbservice service) {
		setSizeFull();
		this.service=service;
		configureForms();
		configureGrids();
		updateGrids();
		closeStateEditor();
		closeDistrictEditor();
		closeImplDistrictEditor();
		add(getToolbar(), getContent());
	}
	
	private Component getToolbar() {
		Button addButton=new  Button("State");
		Button DistrictButton=new  Button("District");
		//Button implDistrictButton=new  Button("Impl District");
		addButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		DistrictButton.setIcon(new Icon(VaadinIcon.PLUS_CIRCLE));
		addButton.addClickListener(e-> addState());
		DistrictButton.addClickListener(e->addDistrict());
		HorizontalLayout toolbar=new HorizontalLayout(addButton, DistrictButton);
		toolbar.setWidthFull();
		return toolbar;
	}
	private void configureForms() {
		stateform=new StateForm(service);
		stateform.setWidth("40%");
		stateform.addListener(StateForm.SaveEvent.class, this::saveState);
		stateform.addListener(StateForm.DeleteEvent.class, this::deleteState);
		stateform.addListener(StateForm.CloseEvent.class, e->closeStateEditor());
		distform=new DistrictForm(service);
		distform.setWidth("40%");
		distform.addListener(DistrictForm.SaveEvent.class, this::saveDistrict);
		distform.addListener(DistrictForm.DeleteEvent.class, this::deleteDistrict);
		distform.addListener(DistrictForm.CloseEvent.class, e->closeDistrictEditor());
		impldistform=new ImpldistrictForm(service);
		impldistform.setWidth("40%");
		impldistform.addListener(ImpldistrictForm.SaveEvent.class, this::saveImplDistrict);
		//impldistform.addListener(ImpldistrictForm.DeleteEvent.class, this::deleteDistrict);
		impldistform.addListener(ImpldistrictForm.DeleteEvent.class, e->closeDistrictEditor());
		
	}
	private void configureGrids() {
		sgrid.setSizeFull();
		ugrid.setSizeFull();
		sgrid.setColumns("stateId", "stateName","stateHq", "stateLabel");
		idgrid.setSizeFull();
		idgrid.setColumns("districtCode", "districtName","districtHq","deputyCommissioner","deputyCommissionerName");
		dgrid.getColumns().forEach(col-> col.setAutoWidth(true));
		dgrid.setSizeFull();
		dgrid.setColumns("districtCode", "districtName","districtHq","deputyCommissioner","deputyCommissionerName","districtAddress","districtEmail", "districtFax","districtPhone", "districtPin", "districtLabel");
		dgrid.addColumn(district-> district.getState().getStateName()).setHeader("State");
		ugrid.setColumns("userId","userName","role");
		ugrid.addColumn(users->users.getDistrict().getDistrictName()).setHeader("District");
		ugrid.addColumn(users->users.getDistrict().getState().getStateName()).setHeader("State");
		dgrid.getColumns().forEach(col-> col.setAutoWidth(true));
		idgrid.getColumns().forEach(col-> col.setAutoWidth(true));
		sgrid.getColumns().forEach(col-> col.setAutoWidth(true));
		sgrid.asSingleSelect().addValueChangeListener(e-> editState(e.getValue()));
		dgrid.asSingleSelect().addValueChangeListener(e-> editDistrict(e.getValue()));
		idgrid.asSingleSelect().addValueChangeListener(e-> editImplDistrict(e.getValue()));
	}
	
	private Component getContent() {
		VerticalLayout vlayout=new VerticalLayout(sgrid, ugrid);
		vlayout.setSizeFull();
		HorizontalLayout content=new HorizontalLayout(vlayout, dgrid, idgrid, stateform, distform, impldistform);
		content.setFlexGrow(1, vlayout);
		content.setFlexGrow(1, dgrid);
		content.setFlexGrow(1, distform);
		content.setFlexGrow(1, impldistform);
		content.setSizeFull();
		return content;
	}
	
	public void updateGrids() {
		dgrid.setItems(service.getAllDistrictsOfAllStates());
		idgrid.setItems(service.getAllImplDistricts());
		sgrid.setItems(service.getAllStates());
		ugrid.setItems(service.getAllUsers());
	}
	private void closeStateEditor() {
		stateform.setState(null);
		stateform.setVisible(false);
		// TODO Auto-generated method stub
		
	}
	private void closeDistrictEditor() {
		distform.setDistrict(null);
		distform.setVisible(false);
		// TODO Auto-generated method stub
		
	}
	private void closeImplDistrictEditor() {
		impldistform.setImpldistrict(null);
		impldistform.setVisible(false);
		// TODO Auto-generated method stub
		
	}
	public void saveState(StateForm.SaveEvent event) {
		service.saveState(event.getState());
		updateGrids();
		closeStateEditor();
	}

	public void deleteState(StateForm.DeleteEvent event) {
		service.deleteState(event.getState());
		updateGrids();
		closeStateEditor();
	}

	private void addState() {
		sgrid.asSingleSelect().clear();
		editState(new State());
	}

	private void editState(State state) {
		// TODO Auto-generated method stub
		stateform.setVisible(false);
		impldistform.setVisible(false);
		distform.setVisible(false);
		if (state == null) {
			closeStateEditor();
		} else {
			stateform.setState(state);
			stateform.setVisible(true);
			//stateform.inUse.setValue(true);
				//constiform.setVisible(false);
		}
	}
	
	
	public void saveDistrict(DistrictForm.SaveEvent event) {
		service.saveDistrict(event.getDistrict());
		updateGrids();
		closeDistrictEditor();
	}

	public void deleteDistrict(DistrictForm.DeleteEvent event) {
		service.deleteDistrict(event.getDistrict());
		updateGrids();
		closeDistrictEditor();
	}

	private void addDistrict() {
		sgrid.asSingleSelect().clear();
		editDistrict(new District());
	}

	private void editDistrict(District district) {
		// TODO Auto-generated method stub
		stateform.setVisible(false);
		impldistform.setVisible(false);
		if (district == null) {
			closeDistrictEditor();
		} else {
			distform.setDistrict(district);
			distform.setVisible(true);
			//stateform.inUse.setValue(true);
				//constiform.setVisible(false);
		}
	}
	public void saveImplDistrict(ImpldistrictForm.SaveEvent event) {
		service.saveImplDistrict(event.getImpldistrict());
		updateGrids();
		closeDistrictEditor();
		closeImplDistrictEditor();
	}

	

	private void addImplDistrict() {
		idgrid.asSingleSelect().clear();
		editImplDistrict(new Impldistrict());
	}

	private void editImplDistrict(Impldistrict district) {
		// TODO Auto-generated method stub
		stateform.setVisible(false);
		distform.setVisible(false);
		if (district == null) {
			closeImplDistrictEditor();
		} else {
			impldistform.setImpldistrict(district);
			impldistform.setVisible(true);
			//stateform.inUse.setValue(true);
				//constiform.setVisible(false);
		}
	}

}
