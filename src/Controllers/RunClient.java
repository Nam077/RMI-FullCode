package Controllers;

import Models.*;
import Views.Certificate;
import Views.Login;
import Views.Manage;
import Views.Register;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

public class RunClient {
    public static Login login = new Login();
    public static Register register = new Register();
    public static Manage manage = new Manage();
    public static Client client = new Client();

    public static Certificate certificate = new Certificate();

    public static void main(String[] args) {

        /////////////////////Xử lý ở phần Login//////////////////////////////
        login.reg_btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login.setVisible(false);
                register.setVisible(true);
            }
        });
        login.submit_btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User userInput = new User();
                userInput.setEmail(login.emailLogin.getText());
                userInput.setPassword(login.passwordLogin.getText());
                try {
                    User userOutput = client.checkLogin(userInput);
                    if (userOutput != null) {
                        login.notification("Login Successful");
                        login.setVisible(false);
                        ArrayList<User> listUser = client.getAllUser();
                        ArrayList<Vehicle> listVehicle = client.getAllVehicle();
                        ArrayList<ManageVehicle> listManageVehicle = client.getAllManageVehicle();
                        addDataToTableManageVehicle(listManageVehicle);
                        addDataToComboBoxNameVehicleManage(listVehicle);
                        addDataToComboBoxNameUserManage(listUser);
                        addDataToTableUser(listUser);
                        addDataToTableVehicle(listVehicle);
                        manage.AuthUser.setText(userOutput.getName());
                        manage.setVisible(true);

                    } else {
                        login.notification("Login Failed");
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        /////// Xử lý ở phần Logut//////////////////////////////
        manage.LogoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //show Dialog exit or login again
                int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    manage.setVisible(false);
                    login.setVisible(true);
                }
            }
        });
        /////////////////////Xử lý ở phần Register//////////////////////////////
        register.login_btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register.setVisible(false);
                login.setVisible(true);
            }
        });
        register.submit_btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User userInput = new User();
                String email = register.emailRegister.getText();
                String password = register.passwordRegister.getText();
                String name = register.nameRegister.getText();
                if (validateRegister(email, password, name)) {
                    userInput.setEmail(email);
                    userInput.setPassword(password);
                    userInput.setName(name);
                    userInput.setRole("User");
                    try {
                        Message message = client.insertUser(userInput);
                        if (message.getCheck()) {
                            register.notification(message.getMessage());
                            register.setVisible(false);
                            login.setVisible(true);
                        } else {
                            register.notification(message.getMessage());
                        }

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        /////////////////////Xử lý ở phần Manage//////////////////////////////
        /////////////////////Xử lý ở phần Manage User//////////////////////////////
        manage.tableUser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //get Model from to tableUser
                DefaultTableModel model = (DefaultTableModel) manage.tableUser.getModel();
                int row = manage.tableUser.getSelectedRow();
                manage.idUser.setText(model.getValueAt(row, 0) + "");
                manage.nameUser.setText(model.getValueAt(row, 1) + "");
                manage.emailUser.setText(model.getValueAt(row, 2) + "");
                manage.passwordUser.setText(model.getValueAt(row, 3) + "");
                manage.addressUser.setText(model.getValueAt(row, 4) + "");
                manage.phoneUser.setText(model.getValueAt(row, 5) + "");
                String dateManage = model.getValueAt(row, 6) + "";
                try {
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateManage);
                    manage.dateUser.setDate(date);
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block

                }

                manage.codeUser.setText(model.getValueAt(row, 7) + "");
                manage.roleUser.setSelectedItem(model.getValueAt(row, 8) + "");


            }
        });
        ///thêm User vào database và load lại table
        manage.addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	///get dữ liệu User từ bàn views
                User userInput = new User();
                String id = manage.idUser.getText();
                String name = manage.nameUser.getText();
                String email = manage.emailUser.getText();
                String password = manage.passwordUser.getText();
                String address = manage.addressUser.getText();
                String phone = manage.phoneUser.getText();
                String date = ((JTextField) manage.dateUser.getDateEditor().getUiComponent()).getText();
                String code = manage.codeUser.getText();
                String role = manage.roleUser.getSelectedItem().toString();
              ///tạo dữ liệu object User từ bàn những biến ở trên
                userInput.setName(name);
                userInput.setEmail(email);
                userInput.setPassword(password);
                userInput.setAddress(address);
                userInput.setPhone(phone);
                userInput.setDate(date);
                userInput.setCode(code);
                userInput.setRole(role);
                try {
                    Message message = client.insertUser(userInput);
                    if (message.getCheck()) {
                        manage.notification(message.getMessage());
                        ArrayList<User> listUser = client.getAllUser();
                        addDataToTableUser(listUser);
                        addDataToComboBoxNameUserManage(listUser);
                    } else {
                        manage.notification(message.getMessage());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        ///sửa User vào database và load lại table
        manage.updateUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User userInput = new User();
                String id = manage.idUser.getText();
                if (id.equals("")) {
                    manage.notification("Please select a user");
                } else {

                    String name = manage.nameUser.getText();
                    String email = manage.emailUser.getText();
                    String password = manage.passwordUser.getText();
                    String address = manage.addressUser.getText();
                    String phone = manage.phoneUser.getText();
                    String date = ((JTextField) manage.dateUser.getDateEditor().getUiComponent()).getText();
                    String code = manage.codeUser.getText();
                    String role = manage.roleUser.getSelectedItem().toString();
                    userInput.setId(Integer.parseInt(id));
                    userInput.setName(name);
                    userInput.setEmail(email);
                    userInput.setPassword(password);
                    userInput.setAddress(address);
                    userInput.setPhone(phone);
                    userInput.setDate(date);
                    userInput.setCode(code);
                    userInput.setRole(role);
                    System.out.println(userInput);
                    try {
                        Message message = client.updateUser(userInput);
                        if (message.getCheck()) {
                            manage.notification(message.getMessage());
                            ArrayList<User> listUser = client.getAllUser();
                            addDataToTableUser(listUser);
                            addDataToComboBoxNameUserManage(listUser);
                            ArrayList<ManageVehicle> manageVehicles = client.getAllManageVehicle();
                            addDataToTableManageVehicle(manageVehicles);
                        } else {
                            manage.notification(message.getMessage());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        ///xóa User vào database và load lại table
        manage.deleteUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                User userInput = new User();
                if (manage.idUser.getText().equals("")) {
                    manage.notification("Please select a user");
                } else {
                    userInput.setId(Integer.parseInt(manage.idUser.getText()));
                    try {
                        Message message = client.deleteUser(userInput);
                        if (message.getCheck()) {
                            manage.notification(message.getMessage());
                            ArrayList<User> listUser = client.getAllUser();
                            addDataToTableUser(listUser);
                            addDataToComboBoxNameUserManage(listUser);
                        } else {
                            manage.notification(message.getMessage());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        //tìm kiếm User theo tất cả các thuộc tính
        manage.searchUser.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String search = manage.searchUser.getText();
                try {
                	 ArrayList<User> listUser  = client.searchUser(search);
                    addDataToTableUser(listUser);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        /////////////////////Xử lý ở phần Manage Vehicle//////////////////////////////
        manage.tableVehicle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //get Model from to tableUser
                DefaultTableModel model = (DefaultTableModel) manage.tableVehicle.getModel();
                int row = manage.tableVehicle.getSelectedRow();
                manage.idVehicle.setText(model.getValueAt(row, 0) + "");
                manage.nameVehicle.setText(model.getValueAt(row, 1) + "");
                manage.colorVehicle.setText(model.getValueAt(row, 2) + "");
                manage.typeVehicle.setText(model.getValueAt(row, 3) + "");
                String date = model.getValueAt(row, 4) + "";
                //date to date format yyyy-mm-dd
                try {
                    Date dateUser = (Date) new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    manage.dateUser.setDate(dateUser);
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block

                }
                manage.seat_capacityVehicle.setText(model.getValueAt(row, 5) + "");
                manage.capacityVehicle.setText(model.getValueAt(row, 6) + "");
                manage.brandVehicle.setText(model.getValueAt(row, 7) + "");
            }
        });
        ///thêm Vehicle vào database và load lại table
        manage.addVehicle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vehicle vehicleInput = new Vehicle();
                String name = manage.nameVehicle.getText();
                String color = manage.colorVehicle.getText();
                String type = manage.typeVehicle.getText();
                String date = ((JTextField) manage.dateUser.getDateEditor().getUiComponent()).getText();
                String seat_capacity = manage.seat_capacityVehicle.getText();
                String capacity = manage.capacityVehicle.getText();
                String brand = manage.brandVehicle.getText();
                vehicleInput.setName(name);
                vehicleInput.setColor(color);
                vehicleInput.setType(type);
                vehicleInput.setDate(date);
                vehicleInput.setSeat_capacity(seat_capacity);
                vehicleInput.setCapacity(capacity);
                vehicleInput.setBrand(brand);
                try {
                    Message message = client.insertVehicle(vehicleInput);
                    if (message.getCheck()) {
                        manage.notification(message.getMessage());
                        ArrayList<Vehicle> listVehicle = client.getAllVehicle();
                        addDataToTableVehicle(listVehicle);
                        addDataToComboBoxNameVehicleManage(listVehicle);
                    } else {
                        manage.notification(message.getMessage());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        ///sửa Vehicle vào database và load lại table
        manage.updateVehicle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vehicle vehicleInput = new Vehicle();
                String id = manage.idVehicle.getText();
                if (id.equals("")) {
                    manage.notification("Please select a vehicle");
                } else {
                    String name = manage.nameVehicle.getText();
                    String color = manage.colorVehicle.getText();
                    String type = manage.typeVehicle.getText();
                    String date = ((JTextField) manage.dateVehicle.getDateEditor().getUiComponent()).getText();
                    String seat_capacity = manage.seat_capacityVehicle.getText();
                    String capacity = manage.capacityVehicle.getText();
                    String brand = manage.brandVehicle.getText();
                    vehicleInput.setId(Integer.parseInt(id));
                    vehicleInput.setName(name);
                    vehicleInput.setColor(color);
                    vehicleInput.setType(type);
                    vehicleInput.setDate(date);
                    vehicleInput.setSeat_capacity(seat_capacity);
                    vehicleInput.setCapacity(capacity);
                    vehicleInput.setBrand(brand);
                    try {
                        Message message = client.updateVehicle(vehicleInput);
                        if (message.getCheck()) {
                            manage.notification(message.getMessage());
                            ArrayList<Vehicle> listVehicle = client.getAllVehicle();
                            addDataToTableVehicle(listVehicle);
                            addDataToComboBoxNameVehicleManage(listVehicle);
                            ArrayList<ManageVehicle> listManageVehicle = client.getAllManageVehicle();
                            addDataToTableManageVehicle(listManageVehicle);
                        } else {
                            manage.notification(message.getMessage());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        ///xóa Vehicle vào database và load lại table
        manage.deleteVehicle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vehicle vehicleInput = new Vehicle();
                String id = manage.idVehicle.getText();
                if (id.equals("")) {
                    manage.notification("Please select a vehicle");
                } else {
                    vehicleInput.setId(Integer.parseInt(id));

                    try {
                        Message message = client.deleteVehicle(vehicleInput);
                        if (message.getCheck()) {
                            manage.notification(message.getMessage());
                            ArrayList<Vehicle> listVehicle = client.getAllVehicle();
                            addDataToTableVehicle(listVehicle);
                            addDataToComboBoxNameVehicleManage(listVehicle);
                            ArrayList<ManageVehicle> manageVehicles = client.getAllManageVehicle();
                            addDataToTableManageVehicle(manageVehicles);
                        } else {
                            manage.notification(message.getMessage());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        ///tìm kiếm Vehicle theo tất cả các thuộc tính
        manage.searchVehicle.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String search = manage.searchVehicle.getText();
                ArrayList<Vehicle> listVehicle = null;
                try {
                    listVehicle = client.searchVehicle(search);
                    addDataToTableVehicle(listVehicle);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        /////////////////////Xử lý ở phần ManageVehicle//////////////////////////////
        manage.tableManage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //get Model from to tableUser
                DefaultTableModel model = (DefaultTableModel) manage.tableManage.getModel();
                int row = manage.tableManage.getSelectedRow();
                manage.idManage.setText(model.getValueAt(row, 0) + "");
                manage.nameUserManage.setSelectedItem(model.getValueAt(row, 1) + "");
                manage.nameManage.setText(model.getValueAt(row, 2) + "");
                manage.nameVehicleManage.setSelectedItem(model.getValueAt(row, 3) + "");

                String dateUser = model.getValueAt(row, 4) + "";
                try {
                    java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateUser);
                    manage.dateManage.setDate(date);
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block

                }
                manage.plateManage.setText(model.getValueAt(row, 5) + "");
                manage.engine_noMange.setText(model.getValueAt(row, 6) + "");
                manage.chassis_noManage.setText(model.getValueAt(row, 7) + "");

            }
        });
        //thêm ManageVehicle vào database và load lại table
        manage.addManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageVehicle manageVehicleInput = new ManageVehicle();
                String id = manage.idManage.getText();
                String nameUser = manage.nameUserManage.getSelectedItem().toString();
                String nameVehicle = manage.nameVehicleManage.getSelectedItem().toString();
                String date = ((JTextField) manage.dateManage.getDateEditor().getUiComponent()).getText();
                String plate = manage.plateManage.getText();
                String name = manage.nameManage.getText();
                String engine_no = manage.engine_noMange.getText();
                String chassis_no = manage.chassis_noManage.getText();
                Integer id_user = getIdFromString(nameUser);
                manageVehicleInput.setUser_id(id_user);
                manageVehicleInput.setName(name);
                Integer id_vehicle = getIdFromString(nameVehicle);
                manageVehicleInput.setVehicle_id(id_vehicle);
                manageVehicleInput.setDate(date);
                manageVehicleInput.setPlate(plate);
                manageVehicleInput.setEngine_no(engine_no);
                manageVehicleInput.setChassis_no(chassis_no);
                System.out.println(manageVehicleInput);
                try {
                    Message message = client.insertManageVehicle(manageVehicleInput);
                    if (message.getCheck()) {
                        manage.notification(message.getMessage());
                        ArrayList<ManageVehicle> listManageVehicle = client.getAllManageVehicle();
                        addDataToTableManageVehicle(listManageVehicle);
                    } else {
                        manage.notification(message.getMessage());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        //sửa ManageVehicle vào database và load lại table
        manage.updateManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageVehicle manageVehicleInput = new ManageVehicle();
                String id = manage.idManage.getText();
                String nameUser = manage.nameUserManage.getSelectedItem().toString();
                String nameVehicle = manage.nameVehicleManage.getSelectedItem().toString();
                String date = ((JTextField) manage.dateManage.getDateEditor().getUiComponent()).getText();
                String plate = manage.plateManage.getText();
                String name = manage.nameManage.getText();
                String engine_no = manage.engine_noMange.getText();
                String chassis_no = manage.chassis_noManage.getText();
                System.out.println(name);
                manageVehicleInput.setId(Integer.parseInt(id));
                Integer id_user = getIdFromString(nameUser);
                manageVehicleInput.setUser_id(id_user);
                Integer id_vehicle = getIdFromString(nameVehicle);
                manageVehicleInput.setEngine_no(engine_no);
                manageVehicleInput.setChassis_no(chassis_no);
                manageVehicleInput.setVehicle_id(id_vehicle);
                manageVehicleInput.setDate(date);
                manageVehicleInput.setPlate(plate);
                manageVehicleInput.setName(name);
                try {
                    Message message = client.updateManageVehicle(manageVehicleInput);
                    if (message.getCheck()) {
                        manage.notification(message.getMessage());
                        ArrayList<ManageVehicle> listManageVehicle = client.getAllManageVehicle();
                        addDataToTableManageVehicle(listManageVehicle);
                    } else {
                        manage.notification(message.getMessage());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        //xóa ManageVehicle vào database và load lại table
        manage.deleteManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ManageVehicle manageVehicle = new ManageVehicle();
                String id = manage.idManage.getText();
                if (id.equals("")) {
                    manage.notification("Please select a row to delete");
                } else {
                    manageVehicle.setId(Integer.parseInt(id));
                    try {
                        Message message = client.deleteManageVehicle(manageVehicle);
                        if (message.getCheck()) {
                            manage.notification(message.getMessage());
                            ArrayList<ManageVehicle> listManageVehicle = client.getAllManageVehicle();
                            addDataToTableManageVehicle(listManageVehicle);
                        } else {
                            manage.notification(message.getMessage());
                        }
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });
        //tìm kiếm ManageVehicle theo tất cả các thuộc tính
        manage.searchManage.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String search = manage.searchManage.getText();
                ArrayList<ManageVehicle> listManageVehicle = null;
                try {
                    listManageVehicle = client.searchManageVehicle(search);
                    addDataToTableManageVehicle(listManageVehicle);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        //view ManageVehicle theo tất cả các thuộc tính
        manage.viewManage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	String id =(manage.idManage.getText());
                if (id == "") {
                    manage.notification("Please select a row to view");
                } else {
                	 int id_manage = Integer.parseInt(id);
                    User user = null;
                    Vehicle vehicle = null;
                    ManageVehicle manageVehicle = null;
                    try {
                        manageVehicle = client.getManageVehicleById(id_manage);
                        user = client.getUserById(manageVehicle.getUser_id());
                        vehicle = client.getVehicleById(manageVehicle.getVehicle_id());
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                    certificate.nameCer.setText(user.getName());
                    certificate.addressCer.setText(user.getAddress());
                    certificate.brandCer.setText(vehicle.getBrand());
                    certificate.modelCer.setText( vehicle.getName());
                    certificate.plateCer.setText(manageVehicle.getPlate());
                    certificate.engineCer.setText(manageVehicle.getEngine_no());
                    certificate.idCer.setText("Số(Number): " + manageVehicle.getId() + "");
                    certificate.chasicCer.setText(manageVehicle.getChassis_no());
                    certificate.colorCer.setText(vehicle.getColor());
                    String date = manageVehicle.getDate();
                    //////2022-06-30 ["2022","06","30"] 
                    String[] dateArray = date.split("-");
                    String year = dateArray[0];
                    String month = dateArray[1];
                    String day = dateArray[2];
                    certificate.dateCer.setText(day);
                    certificate.yearCer.setText(year);
                    certificate.monthCer.setText(month);
                    certificate.setVisible(true);
                }
            }
        });
        //////bạn ơi vào bạn phải demo code bạn demo code cho tôi xem đi chứ tôi 

        certificate.exitCer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                certificate.setVisible(false);

            }
        });
    }


    //validate register email, password, name
    public static boolean validateRegister(String email, String password, String name) {
        //check email not empty
        if (email.isEmpty()) {
            register.notification("Email is empty");
            return false;
        }
        //check email format
        if (!validateEmail(email)) {
            register.notification("Email is invalid");
            return false;
        }
        //check password not empty
        if (password.isEmpty()) {
            register.notification("Password is empty");
            return false;
        }
        //check password length
        if (password.length() < 6) {
            register.notification("Password is too short is less than 6 characters");
            return false;
        }
        //check name not empty
        if (name.isEmpty()) {
            register.notification("Name is empty");
            return false;
        }

        return true;
    }

    public static boolean validateEmail(String email) {
        return Pattern.matches("[_a-zA-Z1-9]+(\\.[A-Za-z0-9]*)*@[A-Za-z0-9]+\\.[A-Za-z0-9]+(\\.[A-Za-z0-9]*)*", email);
    }

    public static void addDataToTableUser(ArrayList<User> listUser) {
        DefaultTableModel modelUser = new DefaultTableModel();
        Object[] columns = {"ID", "Name", "Email", "Password", "Address", "Phone", "Date", "Code", "Role"};
        modelUser = new DefaultTableModel(columns, 0);
        //add data to table
        for (User user : listUser) {
            Object[] row = {user.getId(), user.getName(), user.getEmail(), user.getPassword(), user.getAddress(), user.getPhone(), user.getDate(), user.getCode(), user.getRole()};
            modelUser.addRow(row);
        }
        manage.tableUser.setModel(modelUser);
    }

    //add Data to table VehicleTable
    public static void addDataToTableVehicle(ArrayList<Vehicle> listVehicle) {
        DefaultTableModel modelVehicle = new DefaultTableModel();

        Object[] columns = {"ID", "Name", "Color", "Type", "Date", "Seat Capacity", "Capacity", "Brand"};
        modelVehicle = new DefaultTableModel(columns, 0);
        //add data to table
        for (Vehicle vehicle : listVehicle) {
            Object[] row = {vehicle.getId(), vehicle.getName(), vehicle.getColor(), vehicle.getType(), vehicle.getDate(), vehicle.getSeat_capacity(), vehicle.getCapacity(), vehicle.getBrand()};
            modelVehicle.addRow(row);
        }
        manage.tableVehicle.setModel(modelVehicle);
    }

    //add Data to table ManageTable
    public static void addDataToTableManageVehicle(ArrayList<ManageVehicle> manageVehicles) {
        DefaultTableModel modelManage = new DefaultTableModel();
        Object columns[] = {"ID", "User Name", "Name", "Vehicle Name", "Date", "Plate", "Engine_No", "Chassis_No"};
        modelManage = new DefaultTableModel(columns, 0);
        //add data to table
        for (ManageVehicle manageVehicle : manageVehicles) {
            User user = getUserById(manageVehicle.getUser_id());
            Vehicle vehicle = getVehicleById(manageVehicle.getVehicle_id());
            Object[] row = {manageVehicle.getId(), user.getName() + " - " + user.getId(), manageVehicle.getName(), vehicle.getName() + " - " + vehicle.getId(), manageVehicle.getDate(), manageVehicle.getPlate(), manageVehicle.getEngine_no(), manageVehicle.getChassis_no()};
            modelManage.addRow(row);
        }
        manage.tableManage.setModel(modelManage);
    }

    //get User by id
    public static User getUserById(int id) {
        User user = null;
        try {
            user = client.getUserById(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return user;
    }

    //get Name Vehicle by id
    ///get VehicleById
    public static Vehicle getVehicleById(int id) {
        Vehicle vehicle = new Vehicle();
        try {
            vehicle = client.getVehicleById(id);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return vehicle;
    }

    //add data to combobox nameUserManage
    public static void addDataToComboBoxNameUserManage(ArrayList<User> listUser) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (User user : listUser) {
            model.addElement(user.getName() + " - " + user.getId());
        }
        manage.nameUserManage.setModel(model);

    }

    //add data to combobox nameVehicleManage
    public static void addDataToComboBoxNameVehicleManage(ArrayList<Vehicle> listVehicle) {
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Vehicle vehicle : listVehicle) {

            model.addElement(vehicle.getName() + " - " + vehicle.getId());

        }
        manage.nameVehicleManage.setModel(model);
    }

    public static int getIdFromString(String str) {
        String[] arr = str.split(" - ");
        return Integer.parseInt(arr[1]);
    }


}
