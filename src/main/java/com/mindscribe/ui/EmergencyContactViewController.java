package com.mindscribe.ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.mindscribe.util.ViewSwitcher;
import com.mindscribe.ui.SessionManager;

/**
 * JavaFX Controller for Emergency Contact Management view
 */
public class EmergencyContactViewController {
    
    @FXML
    private Button btnBack;
    
    @FXML
    private TextField txtName;
    
    @FXML
    private TextField txtEmail;
    
    @FXML
    private TextField txtPhone;
    
    @FXML
    private ComboBox<String> comboRelationship;
    
    @FXML
    private CheckBox chkPrimary;
    
    @FXML
    private Button btnAddContact;
    
    @FXML
    private Button btnClearForm;
    
    @FXML
    private TableView<EmergencyContactService.EmergencyContact> contactsTable;
    
    @FXML
    private TableColumn<EmergencyContactService.EmergencyContact, String> nameColumn;
    
    @FXML
    private TableColumn<EmergencyContactService.EmergencyContact, String> emailColumn;
    
    @FXML
    private TableColumn<EmergencyContactService.EmergencyContact, String> phoneColumn;
    
    @FXML
    private TableColumn<EmergencyContactService.EmergencyContact, String> relationshipColumn;
    
    @FXML
    private TableColumn<EmergencyContactService.EmergencyContact, Boolean> primaryColumn;
    
    @FXML
    private TableColumn<EmergencyContactService.EmergencyContact, Void> actionsColumn;
    
    @FXML
    private Label lblTotalContacts;
    
    @FXML
    private Label lblPrimaryContact;
    
    @FXML
    private Button btnTestNotification;
    
    @FXML
    private Label lblStatus;
    
    private EmergencyContactService emergencyService;
    private ObservableList<EmergencyContactService.EmergencyContact> contactsData;
    
    public EmergencyContactViewController() {
        this.emergencyService = new EmergencyContactService();
        this.contactsData = FXCollections.observableArrayList();
    }
    
    @FXML
    public void initialize() {
        // Setup relationship combo box
        setupRelationshipComboBox();
        
        // Setup table columns
        setupTableColumns();
        
        // Load contacts data
        loadContactsData();
        
        // Set table data
        contactsTable.setItems(contactsData);
        
        // Update status labels
        updateStatusLabels();
    }
    
    @FXML
    public void handleBackToDashboard() {
        ViewSwitcher.switchToView("/fxml/DashboardView.fxml", "MindScribe - Dashboard");
    }
    
    @FXML
    public void handleAddContact() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String relationship = comboRelationship.getValue();
        boolean isPrimary = chkPrimary.isSelected();
        
        // Validation
        if (name.isEmpty()) {
            showStatus("Please enter a contact name", "error");
            return;
        }
        
        if (email.isEmpty() || !email.contains("@")) {
            showStatus("Please enter a valid email address", "error");
            return;
        }
        
        if (phone.isEmpty()) {
            showStatus("Please enter a phone number", "error");
            return;
        }
        
        if (relationship == null) {
            showStatus("Please select a relationship", "error");
            return;
        }
        
        try {
            // Create new contact
            EmergencyContactService.EmergencyContact newContact = 
                new EmergencyContactService.EmergencyContact(name, email, phone, relationship, isPrimary);
            
            // Add to service
            emergencyService.addContact(name, email, phone, isPrimary);
            
            // Reload data
            loadContactsData();
            
            // Clear form
            clearForm();
            
            showStatus("Emergency contact added successfully!", "success");
            updateStatusLabels();
            
        } catch (Exception e) {
            showStatus("Error adding contact: " + e.getMessage(), "error");
        }
    }
    
    @FXML
    public void handleClearForm() {
        clearForm();
    }
    
    @FXML
    public void handleTestNotification() {
        try {
            String currentUser = SessionManager.getCurrentUser();
            if (currentUser == null) {
                showStatus("Please log in first", "error");
                return;
            }
            
            boolean sent = emergencyService.getContacts().size() > 0;
            if (sent) {
                // Send test notification (mock)
                System.out.println("Test notification would be sent to " + emergencyService.getContacts().size() + " contacts");
                showStatus("Test notification sent successfully!", "success");
            } else {
                showStatus("No emergency contacts configured. Please add contacts first.", "warning");
            }
        } catch (Exception e) {
            showStatus("Error sending test notification: " + e.getMessage(), "error");
        }
    }
    
    private void setupRelationshipComboBox() {
        comboRelationship.setItems(FXCollections.observableArrayList(
            "Family Member",
            "Friend", 
            "Therapist",
            "Doctor",
            "Partner",
            "Parent",
            "Sibling",
            "Other"
        ));
    }
    
    private void setupTableColumns() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        relationshipColumn.setCellValueFactory(new PropertyValueFactory<>("relationship"));
        
        primaryColumn.setCellValueFactory(param -> {
            if (param.getValue() != null) {
                return new SimpleBooleanProperty(param.getValue().isPrimary()).asObject();
            }
            return new SimpleBooleanProperty(false).asObject();
        });
        
        // Add delete button to actions column
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            
            {
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white;");
                deleteButton.setOnAction(event -> {
                    EmergencyContactService.EmergencyContact contact = getTableView().getItems().get(getIndex());
                    deleteContact(contact);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
    }
    
    private void loadContactsData() {
        try {
            contactsData.clear();
            contactsData.addAll(emergencyService.getContacts());
        } catch (Exception e) {
            showStatus("Error loading contacts: " + e.getMessage(), "error");
        }
    }
    
    private void deleteContact(EmergencyContactService.EmergencyContact contact) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Delete");
        confirmDialog.setHeaderText("Delete Emergency Contact");
        confirmDialog.setContentText("Are you sure you want to delete " + contact.getName() + " from your emergency contacts?");
        
        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                int index = contactsData.indexOf(contact);
                emergencyService.removeContact(index);
                loadContactsData();
                updateStatusLabels();
                showStatus("Contact deleted successfully", "success");
            } catch (Exception e) {
                showStatus("Error deleting contact: " + e.getMessage(), "error");
            }
        }
    }
    
    private void clearForm() {
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        comboRelationship.setValue(null);
        chkPrimary.setSelected(false);
    }
    
    private void updateStatusLabels() {
        int totalContacts = contactsData.size();
        lblTotalContacts.setText("Total Contacts: " + totalContacts);
        
        String primaryContactName = contactsData.stream()
                .filter(EmergencyContactService.EmergencyContact::isPrimary)
                .map(EmergencyContactService.EmergencyContact::getName)
                .findFirst()
                .orElse("None set");
        
        lblPrimaryContact.setText("Primary Contact: " + primaryContactName);
    }
    
    private void showStatus(String message, String type) {
        lblStatus.setText(message);
        lblStatus.setStyle(getStatusStyle(type));
        
        // Clear status after 5 seconds
        if (!message.contains("Error")) {
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(5000);
                    lblStatus.setText("");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }
    
    private String getStatusStyle(String type) {
        switch (type.toLowerCase()) {
            case "success":
                return "-fx-text-fill: #28a745; -fx-font-weight: bold;";
            case "error":
                return "-fx-text-fill: #dc3545; -fx-font-weight: bold;";
            case "warning":
                return "-fx-text-fill: #ffc107; -fx-font-weight: bold;";
            case "info":
                return "-fx-text-fill: #17a2b8; -fx-font-weight: bold;";
            default:
                return "-fx-text-fill: #6c757d;";
        }
    }
}
