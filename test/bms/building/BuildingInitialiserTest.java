package bms.building;

import bms.exceptions.*;
import bms.floor.Floor;
import bms.hazardevaluation.RuleBasedHazardEvaluator;
import bms.hazardevaluation.WeightingBasedHazardEvaluator;
import bms.room.Room;
import bms.room.RoomType;
import bms.sensors.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Use JUnit Test 4 to test the BuildingInitialiser class.*/
public class BuildingInitialiserTest {

    private List<Building> loadBuildings;

    private Building b1;
    private Floor b1_f1, b1_f2, b1_f3, b1_f4, b1_f5;
    private Room b1_r101, b1_r102, b1_r103, b1_r104;
    private Room b1_r201, b1_r202, b1_r203;
    private Room b1_r301, b1_r302, b1_r303;
    private Room b1_r401, b1_r402, b1_r403;
    private Room b1_r501;
    private OccupancySensor b1_102_OS, b1_201_OS, b1_301_OS, b1_501_OS;
    private CarbonDioxideSensor b1_104_CS;
    private NoiseSensor b1_201_NS;
    private TemperatureSensor b1_203_TS, b1_302_TS, b1_303_TS, b1_501_TS;
    private RuleBasedHazardEvaluator b1_r201_RBE;

    private Building b2;
    private Floor b2_f1;
    private Room b2_r101, b2_r102, b2_r103, b2_r104, b2_r105, b2_r106,
            b2_r107, b2_r108, b2_r109, b2_r110;
    private NoiseSensor b2_103_NS;
    private CarbonDioxideSensor b2_107_CS;
    private OccupancySensor b2_107_OS;

    private Building b3;
    private Floor b3_f1;

    private void createCorrectBuildings_b1() {

        b1_102_OS = new OccupancySensor(
                new int[]{13, 24, 28, 15, 6}, 4, 30);
        b1_201_OS = new OccupancySensor(
                new int[]{32, 35, 26, 4, 3, 2, 6, 16, 17, 22, 28, 29}, 2, 40);
        b1_301_OS = new OccupancySensor(
                new int[]{15, 17, 12, 8, 11}, 4, 30);
        b1_501_OS = new OccupancySensor(
                new int[]{15, 12, 2, 0}, 1, 20);

        b1_104_CS = new CarbonDioxideSensor(
                new int[]{690, 740}, 5, 700, 150);

        b1_201_NS = new NoiseSensor(
                new int[]{55,62,69,63},3);

        b1_203_TS = new TemperatureSensor(new int[]{28,29,26,24,25,26});
        b1_302_TS = new TemperatureSensor(new int[]{25,26,24});
        b1_303_TS = new TemperatureSensor(new int[]{24,21});
        b1_501_TS = new TemperatureSensor(new int[]{25,34,61,85});

        b1_r101 = new Room(101,RoomType.STUDY,20);
        b1_r102 = new Room(102,RoomType.STUDY,20);
        b1_r103 = new Room(103,RoomType.STUDY,15);
        b1_r104 = new Room(104,RoomType.LABORATORY,45);

        b1_r201 = new Room(201,RoomType.OFFICE,50);
        b1_r202 = new Room(202,RoomType.OFFICE,30);
        b1_r203 = new Room(203,RoomType.STUDY,10);

        b1_r301 = new Room(301,RoomType.STUDY,30);
        b1_r302 = new Room(302,RoomType.LABORATORY,25);
        b1_r303 = new Room(303,RoomType.LABORATORY,25);

        b1_r401 = new Room(401,RoomType.OFFICE,20);
        b1_r402 = new Room(402,RoomType.OFFICE,10);
        b1_r403 = new Room(403,RoomType.OFFICE,10);
        b1_r501 = new Room(501,RoomType.LABORATORY,30);

        try {
            b1_r102.addSensor(b1_102_OS);
            b1_r201.addSensor(b1_201_OS);
            b1_r301.addSensor(b1_301_OS);
            b1_r501.addSensor(b1_501_OS);

            b1_r104.addSensor(b1_104_CS);
            b1_r201.addSensor(b1_201_NS);

            b1_r203.addSensor(b1_203_TS);
            b1_r302.addSensor(b1_302_TS);
            b1_r303.addSensor(b1_303_TS);
            b1_r501.addSensor(b1_501_TS);
        } catch (DuplicateSensorException e) {
            e.printStackTrace();
        }

        List<HazardSensor> b1_r201_HS = new ArrayList<>();
        for (Sensor sensor : b1_r201.getSensors()) {
            if (sensor instanceof HazardSensor) {
                b1_r201_HS.add((HazardSensor) sensor);
            }
        }

        b1_r201_RBE = new RuleBasedHazardEvaluator(b1_r201_HS);
        b1_r201.setHazardEvaluator(b1_r201_RBE);


        b1_f1 = new Floor(1, 10, 10);
        b1_f2 = new Floor(2, 10, 10);
        b1_f3 = new Floor(3, 10, 8);
        b1_f4 = new Floor(4, 10, 5);
        b1_f5 = new Floor(5, 8, 5);

        try {
            b1_f1.addRoom(b1_r101);
            b1_f1.addRoom(b1_r102);
            b1_f1.addRoom(b1_r103);
            b1_f1.addRoom(b1_r104);

            b1_f2.addRoom(b1_r201);
            b1_f2.addRoom(b1_r202);
            b1_f2.addRoom(b1_r203);

            b1_f3.addRoom(b1_r301);
            b1_f3.addRoom(b1_r302);
            b1_f3.addRoom(b1_r303);

            b1_f4.addRoom(b1_r401);
            b1_f4.addRoom(b1_r402);
            b1_f4.addRoom(b1_r403);

            b1_f5.addRoom(b1_r501);
        } catch (DuplicateRoomException | InsufficientSpaceException e) {
            e.printStackTrace();
        }

        b1 = new Building("General Purpose South");

        try {
            b1.addFloor(b1_f1);
            b1.addFloor(b1_f2);
            b1.addFloor(b1_f3);
            b1.addFloor(b1_f4);
            b1.addFloor(b1_f5);
        } catch (DuplicateFloorException
                | NoFloorBelowException
                | FloorTooSmallException e) {
            e.printStackTrace();
        }
    }

    private void createCorrectBuildings_b2() {
        b2_103_NS = new NoiseSensor(
                new int[]{52,42,53,56},2);

        b2_107_CS = new CarbonDioxideSensor(
                new int[]{745,1320,2782,3216,5043,3528,1970},
                3, 700, 300);

        b2_107_OS = new OccupancySensor(
                new int[]{11,13,13,13,10}, 3, 20);

        b2_r101 = new Room(101,RoomType.STUDY,23.8);
        b2_r102 = new Room(102,RoomType.STUDY,20);
        b2_r103 = new Room(103,RoomType.STUDY,28.5);
        b2_r104 = new Room(104,RoomType.OFFICE,35);
        b2_r105 = new Room(105,RoomType.STUDY,20);
        b2_r106 = new Room(106,RoomType.STUDY,25.5);
        b2_r107 = new Room(107,RoomType.OFFICE,40);
        b2_r108 = new Room(108,RoomType.STUDY,20);
        b2_r109 = new Room(109,RoomType.STUDY,21.2);
        b2_r110 = new Room(110,RoomType.STUDY,20);

        try {
            b2_r103.addSensor(b2_103_NS);
            b2_r107.addSensor(b2_107_CS);
            b2_r107.addSensor(b2_107_OS);
        } catch (DuplicateSensorException e) {
            e.printStackTrace();
        }

        b2_f1 = new Floor(1, 8.5, 40);

        try {
            b2_f1.addRoom(b2_r101);
            b2_f1.addRoom(b2_r102);
            b2_f1.addRoom(b2_r103);
            b2_f1.addRoom(b2_r104);
            b2_f1.addRoom(b2_r105);
            b2_f1.addRoom(b2_r106);
            b2_f1.addRoom(b2_r107);
            b2_f1.addRoom(b2_r108);
            b2_f1.addRoom(b2_r109);
            b2_f1.addRoom(b2_r110);
        } catch (DuplicateRoomException | InsufficientSpaceException e) {
            e.printStackTrace();
        }

        b2 = new Building("Forgan Smith Building");

        try {
            b2.addFloor(b2_f1);
        } catch (DuplicateFloorException
                | NoFloorBelowException
                | FloorTooSmallException e) {
            e.printStackTrace();
        }
    }

    private void createCorrectBuildings_b3() {
        b3_f1 = new Floor(1, 15, 30);
        b3 = new Building("Andrew N. Liveris Building");
        try {
            b3.addFloor(b3_f1);
        } catch (DuplicateFloorException
                | NoFloorBelowException
                | FloorTooSmallException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setup() {
        try {
            loadBuildings = BuildingInitialiser.loadBuildings(
                    "saves/uqstlucia.txt");
        } catch (IOException | FileFormatException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOccupancySensor_b1() {
        createCorrectBuildings_b1();
        OccupancySensor load_102_OS , load_201_OS, load_301_OS, load_501_OS;

        load_102_OS = (OccupancySensor) loadBuildings.get(0)
                .getFloorByNumber(1)
                .getRoomByNumber(102)
                .getSensor("OccupancySensor");

        load_201_OS = (OccupancySensor) loadBuildings.get(0)
                .getFloorByNumber(2)
                .getRoomByNumber(201)
                .getSensor("OccupancySensor");

        load_301_OS = (OccupancySensor) loadBuildings.get(0)
                .getFloorByNumber(3)
                .getRoomByNumber(301)
                .getSensor("OccupancySensor");

        load_501_OS = (OccupancySensor) loadBuildings.get(0)
                .getFloorByNumber(5)
                .getRoomByNumber(501)
                .getSensor("OccupancySensor");

        Assert.assertEquals(b1_102_OS, load_102_OS);
        Assert.assertEquals(b1_201_OS, load_201_OS);
        Assert.assertEquals(b1_301_OS, load_301_OS);
        Assert.assertEquals(b1_501_OS, load_501_OS);

    }

    @Test
    public void testCarbonDioxideSensor_b1() {
        createCorrectBuildings_b1();
        CarbonDioxideSensor load_104_CS = (CarbonDioxideSensor)
                loadBuildings.get(0)
                .getFloorByNumber(1)
                .getRoomByNumber(104)
                .getSensor("CarbonDioxideSensor");

        Assert.assertEquals(b1_104_CS, load_104_CS);
    }

    @Test
    public void testNoiseSensor_b1() {
        createCorrectBuildings_b1();
        NoiseSensor load_201_NS = (NoiseSensor)
                loadBuildings.get(0)
                .getFloorByNumber(2)
                .getRoomByNumber(201)
                .getSensor("NoiseSensor");

        Assert.assertEquals(b1_201_NS, load_201_NS);
    }

    @Test
    public void testTemperatureSensor_b1() {
        createCorrectBuildings_b1();
        TemperatureSensor load_203_TS, load_302_TS, load_303_TS, load_501_TS;

        load_203_TS = (TemperatureSensor) loadBuildings.get(0)
                .getFloorByNumber(2)
                .getRoomByNumber(203)
                .getSensor("TemperatureSensor");

        load_302_TS = (TemperatureSensor) loadBuildings.get(0)
                .getFloorByNumber(3)
                .getRoomByNumber(302)
                .getSensor("TemperatureSensor");

        load_303_TS = (TemperatureSensor) loadBuildings.get(0)
                .getFloorByNumber(3)
                .getRoomByNumber(303)
                .getSensor("TemperatureSensor");

        load_501_TS = (TemperatureSensor) loadBuildings.get(0)
                .getFloorByNumber(5)
                .getRoomByNumber(501)
                .getSensor("TemperatureSensor");

        Assert.assertEquals(b1_203_TS, load_203_TS);
        Assert.assertEquals(b1_302_TS, load_302_TS);
        Assert.assertEquals(b1_303_TS, load_303_TS);
        Assert.assertEquals(b1_501_TS, load_501_TS);
    }

    @Test
    public void testOccupancySensor_b2() {
        createCorrectBuildings_b2();
        OccupancySensor load_107_OS = (OccupancySensor)
                loadBuildings.get(1)
                .getFloorByNumber(1)
                .getRoomByNumber(107)
                .getSensor("OccupancySensor");

        Assert.assertEquals(b2_107_OS, load_107_OS);
    }

    @Test
    public void testCarbonDioxideSensor_b2() {
        createCorrectBuildings_b2();
        CarbonDioxideSensor load_107_CS = (CarbonDioxideSensor)
                loadBuildings.get(1)
                .getFloorByNumber(1)
                .getRoomByNumber(107)
                .getSensor("CarbonDioxideSensor");

        Assert.assertEquals(b2_107_CS, load_107_CS);
    }

    @Test
    public void testNoiseSensor_b2() {
        createCorrectBuildings_b2();
        NoiseSensor load_103_NS = (NoiseSensor) loadBuildings.get(1)
                .getFloorByNumber(1)
                .getRoomByNumber(103)
                .getSensor("NoiseSensor");

        Assert.assertEquals(b2_103_NS, load_103_NS);
    }

    @Test
    public void testRoom_b1() {
        createCorrectBuildings_b1();

        List<Room> load_f1_rooms = loadBuildings.get(0)
                .getFloorByNumber(1)
                .getRooms();

        List<Room> load_f2_rooms = loadBuildings.get(0)
                .getFloorByNumber(2)
                .getRooms();

        List<Room> load_f3_rooms = loadBuildings.get(0)
                .getFloorByNumber(3)
                .getRooms();

        List<Room> load_f4_rooms = loadBuildings.get(0)
                .getFloorByNumber(4)
                .getRooms();

        List<Room> load_f5_rooms = loadBuildings.get(0)
                .getFloorByNumber(5)
                .getRooms();

        Assert.assertEquals(4, load_f1_rooms.size());
        Assert.assertEquals(3, load_f2_rooms.size());
        Assert.assertEquals(3, load_f3_rooms.size());
        Assert.assertEquals(3, load_f4_rooms.size());
        Assert.assertEquals(1, load_f5_rooms.size());

        for (Room loadRoom : load_f1_rooms) {
            switch (loadRoom.getRoomNumber()){
                case 101 -> Assert.assertEquals(b1_r101, loadRoom);
                case 102 -> Assert.assertEquals(b1_r102, loadRoom);
                case 103 -> Assert.assertEquals(b1_r103, loadRoom);
                case 104 -> Assert.assertEquals(b1_r104, loadRoom);
                default -> Assert.fail();
            }
        }

        for (Room loadRoom : load_f2_rooms) {
            switch (loadRoom.getRoomNumber()){
                case 201 -> Assert.assertEquals(b1_r201, loadRoom);
                case 202 -> Assert.assertEquals(b1_r202, loadRoom);
                case 203 -> Assert.assertEquals(b1_r203, loadRoom);
                default -> Assert.fail();
            }
        }

        for (Room loadRoom : load_f3_rooms) {
            switch (loadRoom.getRoomNumber()){
                case 301 -> Assert.assertEquals(b1_r301, loadRoom);
                case 302 -> Assert.assertEquals(b1_r302, loadRoom);
                case 303 -> Assert.assertEquals(b1_r303, loadRoom);
                default -> Assert.fail();
            }
        }

        for (Room loadRoom : load_f4_rooms) {
            switch (loadRoom.getRoomNumber()){
                case 401 -> Assert.assertEquals(b1_r401, loadRoom);
                case 402 -> Assert.assertEquals(b1_r402, loadRoom);
                case 403 -> Assert.assertEquals(b1_r403, loadRoom);
                default -> Assert.fail();
            }
        }

        Assert.assertEquals(b1_r501, load_f5_rooms.get(0));
    }

    @Test
    public void testRuleBaseEvaluator() {
        createCorrectBuildings_b1();
        createCorrectBuildings_b2();

        RuleBasedHazardEvaluator load_RBE_r201 =
                (RuleBasedHazardEvaluator)
                loadBuildings.get(0).getFloorByNumber(2)
                .getRoomByNumber(201)
                        .getHazardEvaluator();

        Assert.assertNotNull(load_RBE_r201);
    }

    @Test
    public void testWeightBaseEvaluator_b1() {
        createCorrectBuildings_b1();

        WeightingBasedHazardEvaluator load_WBE =
                (WeightingBasedHazardEvaluator)
                        loadBuildings.get(0).getFloorByNumber(5)
                                .getRoomByNumber(501)
                                .getHazardEvaluator();

        Assert.assertNotNull(load_WBE);
    }

    @Test
    public void testRoom_b2() {
        createCorrectBuildings_b2();

        List<Room> load_f1_rooms = loadBuildings.get(1)
                .getFloorByNumber(1)
                .getRooms();

        Assert.assertEquals(10, load_f1_rooms.size());

        for (Room loadRoom : load_f1_rooms) {
            switch (loadRoom.getRoomNumber()){
                case 101 -> Assert.assertEquals(b2_r101, loadRoom);
                case 102 -> Assert.assertEquals(b2_r102, loadRoom);
                case 103 -> Assert.assertEquals(b2_r103, loadRoom);
                case 104 -> Assert.assertEquals(b2_r104, loadRoom);
                case 105 -> Assert.assertEquals(b2_r105, loadRoom);
                case 106 -> Assert.assertEquals(b2_r106, loadRoom);
                case 107 -> Assert.assertEquals(b2_r107, loadRoom);
                case 108 -> Assert.assertEquals(b2_r108, loadRoom);
                case 109 -> Assert.assertEquals(b2_r109, loadRoom);
                case 110 -> Assert.assertEquals(b2_r110, loadRoom);
                default -> Assert.fail();
            }
        }
    }

    @Test
    public void testFloor_b1() {
        createCorrectBuildings_b1();
        List<Floor> load_b1_floors = loadBuildings.get(0).getFloors();
        Assert.assertEquals(5, load_b1_floors.size());
        for (Floor loadFloor : load_b1_floors) {
            switch (loadFloor.getFloorNumber()){
                case 1 -> Assert.assertEquals(b1_f1, loadFloor);
                case 2 -> Assert.assertEquals(b1_f2, loadFloor);
                case 3 -> Assert.assertEquals(b1_f3, loadFloor);
                case 4 -> Assert.assertEquals(b1_f4, loadFloor);
                case 5 -> Assert.assertEquals(b1_f5, loadFloor);
                default -> Assert.fail();
            }
        }
    }

    @Test
    public void testFloor_b2() {
        createCorrectBuildings_b2();
        List<Floor> load_b2_floors = loadBuildings.get(1).getFloors();
        Assert.assertEquals(1, load_b2_floors.size());
        Assert.assertEquals(b2_f1, load_b2_floors.get(0));
    }

    @Test
    public void testBuildings() {
        createCorrectBuildings_b1();
        createCorrectBuildings_b2();
        createCorrectBuildings_b3();

        for (Building loadBuilding : loadBuildings) {
            switch (loadBuilding.getName()){
                case "General Purpose South" ->
                        Assert.assertEquals(b1, loadBuilding);
                case "Forgan Smith Building" ->
                        Assert.assertEquals(b2, loadBuilding);
                case "Andrew N. Liveris Building" ->
                        Assert.assertEquals(b3, loadBuilding);
                default -> Assert.fail();
            }
        }
    }

    /**
     * The number of floors specified for a building is not equal
     * to the actual number of floors read from the file for that building.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest1() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest1.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * The number of rooms specified for a floor is not equal to the actual
     * number of rooms read from the file for that floor.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest2() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest2.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * The number of sensors specified for a room is not equal to the number
     * of sensors read from the file for that room.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest3() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest3.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A floor's maintenance schedule contains a room number that does not
     * correspond to a room with the same number on that floor.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest4() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest4.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A floor's maintenance schedule is invalid according to
     * Floor.createMaintenanceSchedule(List).
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest5() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest5.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A building has two floors with the same floor number (a duplicate floor).
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest6() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest6.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A floor's length or width is less than the minimum length or width,
     * respectively, for a floor.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest7() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest7.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A floor has no floor below to support the floor.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest8() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest8.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A floor is too large to fit on top of the floor below.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest9() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest9.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A floor has two rooms with the same room number (a duplicate room).
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest10() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest10.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A room cannot be added to its floor because there is insufficient
     * unoccupied space on the floor.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest11() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest11.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A room's type is not one of the types listed in RoomType.
     * Room types are case-sensitive.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest12() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest12.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A room's area is less than the minimum area for a room.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest13() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest13.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A room's hazard evaluator type is invalid.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest14() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest14.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A room's weighting-based hazard evaluator weightings are invalid
     * according to WeightingBasedHazardEvaluator(Map).
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest15() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest15.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A room has two sensors of the same type (a duplicate sensor).
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest16() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest16.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A sensor's type does not match one of the concrete sensor types
     * (e.g. NoiseSensor, OccupancySensor, ...).
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest17() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest17.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A sensor's update frequency does not meet the restrictions outlined
     * in TimedSensor(int[], int).
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest18() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest18.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * A carbon dioxide sensor's variation limit is greater than its
     * ideal CO2 value.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest19() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest19.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * Any numeric value that should be non-negative is less than zero.
     * This includes:
     *  • the number of floors in a building
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest20() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest20.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * Any numeric value that should be non-negative is less than zero.
     * This includes:
     * 	• the number of rooms on a floor
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest21() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest21.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * Any numeric value that should be non-negative is less than zero.
     * This includes:
     * 	• the number of sensors in room
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest22() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest22.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * Any numeric value that should be non-negative is less than zero.
     * This includes:
     * 	• sensor readings
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest23() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest23.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * Floor numbers should be positive is less than or equal to
     * zero. This includes: floor numbers
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest24() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest24.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * The colon-delimited format is violated, i.e. there are more/fewer
     * colons than expected.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest25() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest25.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * The colon-delimited format is violated, i.e. there are more/fewer
     * colons than expected.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest26() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest26.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }

    /**
     * The colon-delimited format is violated, i.e. there are more/fewer
     * colons than expected.
     */
    @Test(expected = FileFormatException.class)
    public void exceptionTest27() throws IOException, FileFormatException {
        try {
            BuildingInitialiser.loadBuildings(
                    "saves/exceptionTest27.txt");
        } catch (IOException e) {
            throw new IOException();
        } catch (FileFormatException e) {
            throw new FileFormatException();
        }
    }
}
