package com.bsac.config;

import com.bsac.entity.Room;
import com.bsac.entity.Student;
import com.bsac.entity.Guard;
import com.bsac.repository.RoomRepository;
import com.bsac.repository.StudentRepository;
import com.bsac.repository.GuardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(StudentRepository studentRepository, RoomRepository roomRepository, GuardRepository guardRepository) {
        return args -> {
            java.util.Optional<Guard> johnOpt = guardRepository.findAll().stream()
                .filter(gd -> gd.getFirstName().equalsIgnoreCase("John") && gd.getSurname().equalsIgnoreCase("Moyo"))
                .findFirst();
            if (johnOpt.isEmpty()) {
                Guard g = new Guard();
                g.setFirstName("John");
                g.setSurname("Moyo");
                g.setLoginCode("123456");
                guardRepository.save(g);
            } else {
                Guard g = johnOpt.get();
                g.setLoginCode("123456");
                guardRepository.save(g);
            }
            if (studentRepository.count() == 0) {
                Student s1 = new Student();
                s1.setStudentNumber("N001");
                s1.setNationalId("12345");
                s1.setName("Jane Doe");
                s1.setGender("Female");
                s1.setProgram("BSc Computer Science");
                s1.setYearOfStudy("Year 2");
                s1.setSemesterEnrolled("Semester 1");
                s1.setPart("Part II");
                studentRepository.save(s1);

                Student s2 = new Student();
                s2.setStudentNumber("N002");
                s2.setNationalId("54321");
                s2.setName("John Doe");
                s2.setGender("Male");
                s2.setProgram("BEng Civil Engineering");
                s2.setYearOfStudy("Year 3");
                s2.setSemesterEnrolled("Semester 1");
                s2.setPart("Part III");
                studentRepository.save(s2);
            }

            if (roomRepository.count() == 0) {
                java.util.List<Room> rooms = new java.util.ArrayList<>();
                // Block 1: Rooms 1 to 299 (Girls)
                for (int i = 1; i <= 299; i++) {
                    Room r = new Room();
                    r.setBlockName("1");
                    r.setRoomNumber(String.valueOf(i));
                    r.setDesignatedGender("Female");
                    r.setRentFee(400.0);
                    r.setOccupied(false);
                    rooms.add(r);
                }
                
                // Block 2: Rooms 300 to 401 (Boys)
                for (int i = 300; i <= 401; i++) {
                    Room r = new Room();
                    r.setBlockName("2");
                    r.setRoomNumber(String.valueOf(i));
                    r.setDesignatedGender("Male");
                    r.setRentFee(400.0);
                    r.setOccupied(false);
                    rooms.add(r);
                }

                // Block 3: Rooms 402 to 600 (Boys)
                for (int i = 402; i <= 600; i++) {
                    Room r = new Room();
                    r.setBlockName("3");
                    r.setRoomNumber(String.valueOf(i));
                    r.setDesignatedGender("Male");
                    r.setRentFee(400.0);
                    r.setOccupied(false);
                    rooms.add(r);
                }
                roomRepository.saveAll(rooms);
            }
        };
    }
}
