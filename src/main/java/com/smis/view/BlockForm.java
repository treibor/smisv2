package com.smis.view;

import com.smis.dbservice.Dbservice;
import com.smis.entity.Block;
import com.smis.entity.master.MasterBlock;
import com.smis.util.ButtonUtil;
import com.smis.util.ValidationUtil;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class BlockForm extends FormLayout{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Dbservice service;
	Binder<Block> binder=new BeanValidationBinder<>(Block.class);
	//TextField blockName=new TextField("Block/MB Name");
	TextField bdoName=new TextField("Office Head");
	TextField blockLabel=new TextField("Block/MB Label");
	Button save= new Button("Save");
	Button delete= new Button("Delete");
	Checkbox inUse=new Checkbox("In Use");
	private Block block;
	boolean isAdmin;
	ComboBox<MasterBlock> masterBlock = new ComboBox<>("Block");
	public Button addButton=new  Button("New");
	public BlockForm(Dbservice service) {
		this.service=service;
		binder.bindInstanceFields(this);
		isAdmin = service.hasRole("ADMIN");
		
		//blockName.setHelperText("Eg: Mawlai or Shillong Municipal Board");
		bdoName.setHelperText("Eg: Block Development Officer");
		blockLabel.setHelperText("Eg: Mawlai C&RD Block");
		ValidationUtil.applyTextOnly(bdoName);
		ValidationUtil.applyValidation(blockLabel);
		masterBlock.setItems(service.getMasterBlocks());
		masterBlock.setItemLabelGenerator(masterblock->masterblock.getBlockName());
		add(new Span("* Click New Button To Add New Item"), masterBlock,bdoName, blockLabel, inUse, createButtonsLayout());
	}
	
	private Component createButtonsLayout() {
		// TODO Auto-generated method stub
		save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		save.addClickShortcut(Key.ENTER);
		save.addClickListener(event-> validateandSave());
		delete.addClickListener(event-> fireEvent(new DeleteEvent(this, block)));
		//delete.setEnabled(isadmin);
		delete.setEnabled(isAdmin);
		ButtonUtil.applySaveStyle(save);
		ButtonUtil.applyDeleteStyle(delete);
		ButtonUtil.applyNewStyle(addButton);
		addButton.addClickListener(event->setBlock(new Block()));
		return new HorizontalLayout(save, delete, addButton);
		
	}
	private void validateandSave() {
		try {
			binder.writeBean(block);
			block.setDistrict(service.getDistrict());
			fireEvent(new SaveEvent(this, block));
			//Notification.show("Block Added Successfully", 5000, Position.BOTTOM_END).addThemeVariants(NotificationVariant.LUMO_SUCCESS);
		} catch (ValidationException e) {
			Notification.show("Please Enter All Fields", 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
			
		}catch (Exception e) {
			Notification.show("Please Contact Your Administrator: Error Code:"+e.getMessage(), 5000, Position.TOP_CENTER).addThemeVariants(NotificationVariant.LUMO_ERROR);
		}
		
	}

	public void setBlock(Block block) {
		this.block=block;
		binder.readBean(block);
		save.setEnabled(true);
		delete.setEnabled(false);
	}
	
	public static abstract class BlockFormEvent extends ComponentEvent<BlockForm> {
		private Block block;

		protected BlockFormEvent(BlockForm source, Block block) {
			super(source, false);
			this.block = block;
		}

		public Block getBlock() {
			return block;
		}
	}

	public static class SaveEvent extends BlockFormEvent {
		SaveEvent(BlockForm source, Block block) {
			super(source, block);
		}
	}

	public static class DeleteEvent extends BlockFormEvent {
		DeleteEvent(BlockForm source, Block block) {
			super(source, block);
		}

	}

	public static class CloseEvent extends BlockFormEvent {
		CloseEvent(BlockForm source) {
			super(source, null);
		}
	}

	public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
			ComponentEventListener<T> listener) {
		return getEventBus().addListener(eventType, listener);
	}
}
