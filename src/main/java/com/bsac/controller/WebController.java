package com.bsac.controller;

import com.bsac.entity.*;
import com.bsac.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class WebController {

    @Autowired private StudentRepository studentRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private StorageItemRepository storageItemRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private RuleRepository ruleRepository;
    @Autowired private VisitorPassRepository visitorPassRepository;
    @Autowired private AnnouncementRepository announcementRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private GuardRepository guardRepository;

    private Student getLoggedInStudent(Authentication authentication) {
        return studentRepository.findByStudentNumber(authentication.getName()).orElse(null);
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerStudent(
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam String studentNumber,
            @RequestParam String nationalId,
            @RequestParam(required = false) String program,
            @RequestParam(required = false) String yearOfStudy,
            @RequestParam(required = false) String semesterEnrolled,
            @RequestParam(required = false) String part,
            Model model) {

        String sNum = studentNumber != null ? studentNumber.trim().toUpperCase() : "";
        String natId = nationalId != null ? nationalId.trim().toUpperCase() : "";

        if (!sNum.matches("^N\\d{8}[A-Z]$")) {
            model.addAttribute("error", "Invalid NUST Student Number. Format must be: N + 8 digits + 1 letter (e.g. N01234567S).");
            return "register";
        }

        if (!natId.matches("^\\d{2}-?\\d{6,7}\\s?[A-Z]\\s?-?\\d{2}$")) {
            model.addAttribute("error", "Invalid Zimbabwe National ID format. Example: 08-1234567-F-43");
            return "register";
        }

        if (studentRepository.findByStudentNumber(sNum).isPresent()) {
            model.addAttribute("error", "An account with this Student Number already exists.");
            return "register";
        }

        Student student = new Student();
        student.setName(name);
        student.setGender(gender);
        student.setStudentNumber(sNum);
        student.setNationalId(natId);
        student.setProgram(program);
        student.setYearOfStudy(yearOfStudy);
        student.setSemesterEnrolled(semesterEnrolled);
        student.setPart(part);
        studentRepository.save(student);

        return "redirect:/login?registered=true";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        model.addAttribute("student", student);
        List<Booking> bookings = bookingRepository.findByStudent(student);
        model.addAttribute("bookings", bookings);
        model.addAttribute("announcements", announcementRepository.findAll());
        List<com.bsac.entity.VisitorPass> passes = visitorPassRepository.findByStudent(student);
        model.addAttribute("visitorPasses", passes);
        model.addAttribute("car", carRepository.findByStudent(student).orElse(null));

        // Checkout warning: notify at 16:30 if student has active visitor passes
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime warningStart = java.time.LocalTime.of(16, 30);
        java.time.LocalTime checkoutTime = java.time.LocalTime.of(17, 0);
        boolean hasActivePasses = passes.stream().anyMatch(p -> "ACTIVE".equals(p.getStatus()));
        boolean showCheckoutWarning = hasActivePasses
                && !now.isBefore(warningStart)
                && now.isBefore(checkoutTime);
        model.addAttribute("showCheckoutWarning", showCheckoutWarning);

        // Server time components for JS auto-refresh scheduling
        model.addAttribute("serverHour", now.getHour());
        model.addAttribute("serverMinute", now.getMinute());
        return "dashboard";
    }

    @GetMapping("/booking")
    public String bookingPage(Model model, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        boolean hasBooking = bookingRepository.findByStudent(student).stream()
                .anyMatch(b -> !b.getStatus().contains("CANCELLED"));
        if (hasBooking) {
            return "redirect:/dashboard?hasRoom=true";
        }
        List<Room> allRooms = roomRepository.findAll();
        List<Room> availableRooms = new java.util.ArrayList<>();
        List<Booking> allBookings = bookingRepository.findAll();
        for (Room room : allRooms) {
            long activeBookings = allBookings.stream().filter(b -> b.getRoom().getId().equals(room.getId())).count();
            if (activeBookings < 2 && student.getGender().equalsIgnoreCase(room.getDesignatedGender())) {
                availableRooms.add(room);
            }
        }
        model.addAttribute("rooms", availableRooms);
        return "booking";
    }

    @GetMapping("/booking/payment")
    public String paymentPage(@RequestParam Long roomId, Model model, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        boolean hasBooking = bookingRepository.findByStudent(student).stream()
                .anyMatch(b -> !b.getStatus().contains("CANCELLED"));
        if (hasBooking) {
            return "redirect:/dashboard?hasRoom=true";
        }
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            long activeBookings = bookingRepository.findAll().stream()
                    .filter(b -> b.getRoom().getId().equals(roomId))
                    .count();
            if (activeBookings >= 2) {
                return "redirect:/booking";
            }
        } else {
            return "redirect:/booking";
        }
        model.addAttribute("room", room);
        model.addAttribute("student", student);
        return "payment";
    }

    // Complete booking with payment
    @PostMapping("/booking/book")
    public String bookRoom(
            @RequestParam Long roomId,
            @RequestParam(defaultValue = "ECOCASH") String paymentMethod,
            @RequestParam(required = false) String paymentReference,
            Authentication auth) {

        Student student = getLoggedInStudent(auth);
        boolean hasBooking = bookingRepository.findByStudent(student).stream()
                .anyMatch(b -> !b.getStatus().contains("CANCELLED"));
        if (hasBooking) {
            return "redirect:/dashboard?hasRoom=true";
        }
        Room room = roomRepository.findById(roomId).orElse(null);

        if (room != null) {
            long activeBookings = bookingRepository.findAll().stream()
                    .filter(b -> b.getRoom().getId().equals(roomId))
                    .count();
            if (activeBookings < 2) {
                Booking booking = new Booking();
                booking.setStudent(student);
                booking.setRoom(room);
                booking.setSemester("2026 Semester 1");
                booking.setAmountPaid(room.getRentFee() != null ? room.getRentFee() : 400.0);
                booking.setPaymentMethod(paymentMethod);
                booking.setPaymentReference(paymentReference);
                booking.setStatus("PENDING ADMIN APPROVAL (Paid via " + paymentMethod + ")");
                bookingRepository.save(booking);

                if (activeBookings + 1 >= 2) {
                    room.setOccupied(true);
                    roomRepository.save(room);
                }
            }
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/storage")
    public String storagePage(Model model, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("items", storageItemRepository.findByStudent(student));
        return "storage";
    }

    @PostMapping("/storage/add")
    public String addStorageItem(
            @RequestParam String itemName,
            @RequestParam String description,
            @RequestParam String paymentMethod,
            @RequestParam String paymentReference,
            Authentication auth) {
        Student student = getLoggedInStudent(auth);
        StorageItem item = new StorageItem();
        item.setStudent(student);
        item.setItemName(itemName);
        item.setDescription(description);
        item.setPaymentMethod(paymentMethod);
        item.setPaymentReference(paymentReference);
        item.setStatus("PENDING ADMIN APPROVAL (Paid via " + paymentMethod + ")");
        storageItemRepository.save(item);
        return "redirect:/storage";
    }

    @GetMapping("/complaints")
    public String complaintsPage(Model model, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        model.addAttribute("complaints", complaintRepository.findByStudent(student));
        model.addAttribute("bookings", bookingRepository.findByStudent(student));
        return "complaints";
    }

    @PostMapping("/complaints/add")
    public String addComplaint(@RequestParam(required = false) Long roomId, @RequestParam String description, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        Room room = null;
        if (roomId != null) {
            room = roomRepository.findById(roomId).orElse(null);
        }
        Complaint complaint = new Complaint();
        complaint.setStudent(student);
        complaint.setRoom(room);
        complaint.setDescription(description);
        complaintRepository.save(complaint);
        return "redirect:/complaints";
    }

    // ── ADMIN PORTAL & AUTHENTICATION ──

    @GetMapping("/admin")
    public String adminPage(Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            return "redirect:/dashboard?error=unauthorized";
        }

        Admin admin = adminRepository.findByEmail(auth.getName().trim().toLowerCase()).orElse(null);
        model.addAttribute("admin", admin);
        model.addAttribute("adminEmail", auth.getName());

        List<Booking> bookings = bookingRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        List<Complaint> complaints = complaintRepository.findAll();
        List<StorageItem> storageItems = storageItemRepository.findAll();

        long roomsAvailable = rooms.stream().filter(r -> {
            long activeBookings = bookings.stream().filter(b -> b.getRoom().getId().equals(r.getId())).count();
            return activeBookings < 2;
        }).count();
        long openComplaints = complaints.stream().filter(c -> "PENDING".equalsIgnoreCase(c.getStatus())).count();

        // Calculate total amount received
        double totalAmountReceived = bookings.stream()
                .filter(b -> !b.getStatus().contains("PENDING"))
                .mapToDouble(b -> 400.0)
                .sum();

        // Group storage items by student
        java.util.Map<Student, List<StorageItem>> groupedStorage = storageItems.stream()
                .collect(java.util.stream.Collectors.groupingBy(StorageItem::getStudent));

        java.util.Map<Long, Booking> studentBookings = bookings.stream()
                .filter(b -> !b.getStatus().contains("CANCELLED"))
                .collect(java.util.stream.Collectors.toMap(b -> b.getStudent().getId(), b -> b, (b1, b2) -> b1));

        model.addAttribute("bookings", bookings);
        model.addAttribute("rooms", rooms);
        model.addAttribute("complaints", complaints);
        model.addAttribute("groupedStorage", groupedStorage);
        model.addAttribute("rules", ruleRepository.findAll());
        model.addAttribute("announcements", announcementRepository.findAll());
        model.addAttribute("visitorPasses", visitorPassRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("studentBookings", studentBookings);
        model.addAttribute("guards", guardRepository.findAll());

        model.addAttribute("totalBookingsCount", bookings.size());
        model.addAttribute("roomsAvailableCount", roomsAvailable);
        model.addAttribute("openComplaintsCount", openComplaints);
        model.addAttribute("storageRequestsCount", storageItems.size());
        model.addAttribute("totalAmountReceived", totalAmountReceived);
        model.addAttribute("guardsCount", guardRepository.count());

        return "admin";
    }

    @PostMapping("/profile/picture")
    public String updateProfilePicture(@RequestParam String profilePictureUrl, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        if (student != null) {
            student.setProfilePictureUrl(profilePictureUrl);
            studentRepository.save(student);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/profile/picture")
    public String updateAdminProfilePicture(@RequestParam String profilePictureUrl, Authentication auth) {
        Admin admin = adminRepository.findByEmail(auth.getName().trim().toLowerCase()).orElse(null);
        if (admin != null) {
            admin.setProfilePictureUrl(profilePictureUrl);
            adminRepository.save(admin);
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/booking/approve/{id}")
    public String approveBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            booking.setStatus("SECURED (Approved by Admin)");
            bookingRepository.save(booking);
        }
        return "redirect:/admin";
    }

    @PostMapping("/admin/booking/remove/{id}")
    public String removeBooking(@PathVariable Long id) {
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking != null) {
            Room room = booking.getRoom();
            if (room != null) {
                room.setOccupied(false);
                roomRepository.save(room);
            }
            bookingRepository.delete(booking);
        }
        return "redirect:/admin";
    }

    @GetMapping("/rules")
    public String rulesPage(Model model, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        model.addAttribute("student", student);
        model.addAttribute("rules", ruleRepository.findAll());
        return "rules";
    }

    @PostMapping("/admin/rules/add")
    public String addRule(@RequestParam String content) {
        Rule rule = new Rule();
        rule.setContent(content);
        ruleRepository.save(rule);
        return "redirect:/admin";
    }

    @PostMapping("/admin/rules/delete/{id}")
    public String deleteRule(@PathVariable Long id) {
        ruleRepository.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping("/visitor/add")
    public String addVisitorPass(
            @RequestParam String visitorName,
            @RequestParam String visitorPhone,
            Authentication auth) {
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime start = java.time.LocalTime.of(13, 0);
        java.time.LocalTime end = java.time.LocalTime.of(17, 0);
        if (now.isBefore(start) || !now.isBefore(end)) {
            return "redirect:/dashboard?timeError=true";
        }
        Student student = getLoggedInStudent(auth);
        if (student != null) {
            VisitorPass pass = new VisitorPass();
            pass.setVisitorName(visitorName);
            pass.setVisitorPhone(visitorPhone);
            pass.setStudent(student);

            // Generate secure random 6 digit code
            String code = String.format("%06d", new java.util.Random().nextInt(900000) + 100000);
            pass.setGateCode(code);

            visitorPassRepository.save(pass);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/visitor/mark-left/{id}")
    public String markVisitorLeft(@PathVariable Long id, Authentication auth) {
        Student student = getLoggedInStudent(auth);
        VisitorPass pass = visitorPassRepository.findById(id).orElse(null);
        if (pass != null && student != null && pass.getStudent().getId().equals(student.getId())) {
            pass.setStatus("HOST_REPORTED_LEFT");
            visitorPassRepository.save(pass);
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/announcements/add")
    public String addAnnouncement(@RequestParam String title, @RequestParam String content) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcementRepository.save(announcement);
        return "redirect:/admin";
    }

    @PostMapping("/admin/announcements/delete/{id}")
    public String deleteAnnouncement(@PathVariable Long id) {
        announcementRepository.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/complaint/resolve/{id}")
    public String resolveComplaint(@PathVariable Long id) {
        Complaint complaint = complaintRepository.findById(id).orElse(null);
        if (complaint != null) {
            complaint.setStatus("RESOLVED");
            complaintRepository.save(complaint);
        }
        return "redirect:/admin";
    }

    @GetMapping("/admin/register")
    public String adminRegisterPage() {
        return "admin_register";
    }

    @PostMapping("/admin/register")
    public String registerAdmin(@RequestParam String email, Model model) {
        String trimmedEmail = email.trim().toLowerCase();
        if (!trimmedEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            model.addAttribute("error", "Invalid email address format.");
            return "admin_register";
        }

        // Generate a 6-digit random verification code
        int code = (int)(Math.random() * 900000) + 100000;
        String verificationCode = String.valueOf(code);

        Admin admin = adminRepository.findByEmail(trimmedEmail).orElse(new Admin());
        admin.setEmail(trimmedEmail);
        admin.setVerificationCode(verificationCode);
        admin.setVerified(false);
        adminRepository.save(admin);

        // System output for testing / verification code capture
        System.out.println("=========================================");
        System.out.println("ADMIN VERIFICATION CODE FOR " + trimmedEmail + " : " + verificationCode);
        System.out.println("=========================================");

        return "redirect:/admin/verify?email=" + trimmedEmail + "&code=" + verificationCode;
    }

    @GetMapping("/admin/verify")
    public String adminVerifyPage(@RequestParam String email, @RequestParam(required = false) String code, Model model) {
        model.addAttribute("email", email);
        if (code != null) {
            model.addAttribute("simulatedCode", code); // For easy UI display/testing
        }
        return "admin_verify";
    }

    @PostMapping("/admin/verify")
    public String verifyAdminCode(@RequestParam String email, @RequestParam String code, Model model) {
        Admin admin = adminRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (admin == null || !code.trim().equals(admin.getVerificationCode())) {
            model.addAttribute("email", email);
            model.addAttribute("error", "Invalid verification code.");
            return "admin_verify";
        }
        return "redirect:/admin/create-password?email=" + email;
    }

    @GetMapping("/admin/create-password")
    public String adminCreatePasswordPage(@RequestParam String email, Model model) {
        model.addAttribute("email", email);
        return "admin_password";
    }

    @PostMapping("/admin/create-password")
    public String saveAdminPassword(@RequestParam String email, @RequestParam String password, Model model) {
        Admin admin = adminRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
        if (admin == null) {
            model.addAttribute("error", "An error occurred. Please try again.");
            return "admin_register";
        }
        admin.setPassword(password);
        admin.setVerified(true);
        adminRepository.save(admin);
        return "redirect:/login?registered=true";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin_login";
    }

    @PostMapping("/admin/student/delete/{id}")
    public String deleteStudent(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            bookingRepository.deleteAll(bookingRepository.findByStudent(student));
            visitorPassRepository.deleteAll(visitorPassRepository.findByStudent(student));
            storageItemRepository.deleteAll(storageItemRepository.findByStudent(student));
            complaintRepository.deleteAll(complaintRepository.findByStudent(student));
            carRepository.findByStudent(student).ifPresent(c -> carRepository.delete(c));
            studentRepository.delete(student);
        }
        return "redirect:/admin";
    }

    @PostMapping("/car/register")
    public String registerCar(
            @RequestParam String regNumber,
            @RequestParam String carName,
            @RequestParam String carColor,
            Authentication auth) {
        Student student = getLoggedInStudent(auth);
        if (student != null) {
            Car car = carRepository.findByStudent(student).orElse(new Car());
            car.setRegNumber(regNumber);
            car.setCarName(carName);
            car.setCarColor(carColor);
            car.setStudent(student);
            carRepository.save(car);
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/guard")
    public String guardPortal(Model model, javax.servlet.http.HttpSession session) {
        Guard loggedInGuard = (Guard) session.getAttribute("loggedInGuard");
        if (loggedInGuard == null) {
            return "redirect:/guard/login";
        }
        model.addAttribute("guard", loggedInGuard);
        return "guard";
    }

    @PostMapping("/guard/verify")
    public String verifyGateCode(@RequestParam String gateCode, Model model, javax.servlet.http.HttpSession session) {
        Guard loggedInGuard = (Guard) session.getAttribute("loggedInGuard");
        if (loggedInGuard == null) {
            return "redirect:/guard/login";
        }
        model.addAttribute("guard", loggedInGuard);
        model.addAttribute("gateCode", gateCode);
        java.util.Optional<VisitorPass> passOpt = visitorPassRepository.findAll().stream()
                .filter(p -> p.getGateCode().trim().equals(gateCode.trim()))
                .findFirst();
        if (passOpt.isPresent()) {
            VisitorPass pass = passOpt.get();
            model.addAttribute("status", "VALID");
            model.addAttribute("pass", pass);
            Booking hostBooking = bookingRepository.findByStudent(pass.getStudent()).stream()
                    .filter(b -> !b.getStatus().contains("CANCELLED"))
                    .findFirst().orElse(null);
            model.addAttribute("hostBooking", hostBooking);
        } else {
            model.addAttribute("status", "INVALID");
        }
        return "guard";
    }

    @PostMapping("/guard/visitor/verify-left/{id}")
    public String verifyVisitorLeft(@PathVariable Long id, javax.servlet.http.HttpSession session, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Guard loggedInGuard = (Guard) session.getAttribute("loggedInGuard");
        if (loggedInGuard == null) {
            return "redirect:/guard/login";
        }
        VisitorPass pass = visitorPassRepository.findById(id).orElse(null);
        if (pass != null) {
            pass.setStatus("VERIFIED_LEFT");
            visitorPassRepository.save(pass);
            redirectAttributes.addFlashAttribute("verificationSuccess", "Visitor " + pass.getVisitorName() + " has been successfully checked out.");
        }
        return "redirect:/guard";
    }

    @GetMapping("/guard/login")
    public String guardLoginPage() {
        return "redirect:/login?role=guard";
    }

    @PostMapping("/guard/login")
    public String handleGuardLogin(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String surname,
            @RequestParam(required = false) String loginCode,
            javax.servlet.http.HttpSession session,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        if (firstName == null || surname == null || loginCode == null || 
            firstName.trim().isEmpty() || surname.trim().isEmpty() || loginCode.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("guardError", "All login fields are required.");
            return "redirect:/login?role=guard";
        }

        String fName = firstName != null ? firstName.trim() : "";
        String sName = surname != null ? surname.trim() : "";
        String code = loginCode != null ? loginCode.trim() : "";

        System.out.println("=== GUARD LOGIN DEBUG ===");
        System.out.println("Submitted First Name: '" + fName + "'");
        System.out.println("Submitted Surname: '" + sName + "'");
        System.out.println("Submitted Code: '" + code + "'");
        
        java.util.List<Guard> allGuards = guardRepository.findAll();
        System.out.println("Total Guards in DB: " + allGuards.size());
        for (Guard g : allGuards) {
            System.out.println(" - Guard in DB: id=" + g.getId() + ", FirstName='" + g.getFirstName() + "', Surname='" + g.getSurname() + "', Code='" + g.getLoginCode() + "'");
        }

        java.util.Optional<Guard> guardOpt = allGuards.stream()
                .filter(g -> g.getFirstName().equalsIgnoreCase(fName) 
                          && g.getSurname().equalsIgnoreCase(sName) 
                          && g.getLoginCode().equals(code))
                .findFirst();

        if (guardOpt.isPresent()) {
            System.out.println("Login SUCCESS for guard: " + guardOpt.get().getFirstName());
            session.setAttribute("loggedInGuard", guardOpt.get());
            return "redirect:/guard";
        } else {
            System.out.println("Login FAILED.");
            redirectAttributes.addFlashAttribute("guardError", "Invalid guard credentials or access code.");
            return "redirect:/login?role=guard";
        }
    }

    @GetMapping("/guard/logout")
    public String guardLogout(javax.servlet.http.HttpSession session) {
        session.removeAttribute("loggedInGuard");
        return "redirect:/login?role=guard";
    }

    @PostMapping("/admin/guard/add")
    public String addGuard(
            @RequestParam String firstName,
            @RequestParam String surname,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        Guard guard = new Guard();
        guard.setFirstName(firstName.trim());
        guard.setSurname(surname.trim());
        
        // Generate secure 6-digit registration code
        int codeNum = (int)(Math.random() * 900000) + 100000;
        String generatedCode = String.valueOf(codeNum);
        guard.setLoginCode(generatedCode);
        
        guardRepository.save(guard);
        
        // Add success notification with the generated code to flash model
        redirectAttributes.addFlashAttribute("guardSuccess", "Guard " + firstName.trim() + " " + surname.trim() + " registered successfully! Security Access Code: " + generatedCode);
        return "redirect:/admin";
    }

    @PostMapping("/admin/guard/delete/{id}")
    public String deleteGuard(@PathVariable Long id) {
        guardRepository.deleteById(id);
        return "redirect:/admin";
    }

    @PostMapping("/admin/storage/approve/{id}")
    public String approveStorage(@PathVariable Long id) {
        StorageItem item = storageItemRepository.findById(id).orElse(null);
        if (item != null) {
            item.setStatus("APPROVED / ACTIVE");
            storageItemRepository.save(item);
        }
        return "redirect:/admin";
    }
}
