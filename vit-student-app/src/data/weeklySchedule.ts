export type ClassEntry = {
  course: string;
  faculty: string;
  start: string;
  end: string;
  room: string;
};

export type WeeklySchedule = {
  [day: string]: ClassEntry[];
};

export const WEEKLY_SCHEDULE: WeeklySchedule = {
  Monday: [
    { course: 'CSE1001', faculty: 'Prof. Rao', start: '08:00', end: '08:50', room: '101' },
    { course: 'MAT1002', faculty: 'Dr. Singh', start: '09:00', end: '09:50', room: '201' },
    { course: 'PHY1003', faculty: 'Dr. Patel', start: '10:00', end: '10:50', room: 'Lab 1' },
    { course: 'ENG1004', faculty: 'Ms. James', start: '11:00', end: '11:50', room: '305' },
    { course: 'CHE1005', faculty: 'Dr. Verma', start: '12:00', end: '12:50', room: '202' },
    { course: 'CSE2001', faculty: 'Prof. Gupta', start: '13:00', end: '13:50', room: '102' },
    { course: 'MAT2002', faculty: 'Dr. Mehta', start: '14:00', end: '14:50', room: '203' },
    { course: 'PHY2003', faculty: 'Dr. Shah', start: '15:00', end: '15:50', room: 'Lab 2' },
    { course: 'ENG2004', faculty: 'Ms. Clark', start: '16:00', end: '16:50', room: '306' },
  ],
  Tuesday: [
    { course: 'CHE1005', faculty: 'Dr. Verma', start: '08:00', end: '08:50', room: '202' },
    { course: 'CSE1001', faculty: 'Prof. Rao', start: '09:00', end: '09:50', room: '101' },
    { course: 'MAT1002', faculty: 'Dr. Singh', start: '10:00', end: '10:50', room: '201' },
    { course: 'PHY1003', faculty: 'Dr. Patel', start: '11:00', end: '11:50', room: 'Lab 1' },
    { course: 'CHE2005', faculty: 'Dr. Jain', start: '12:00', end: '12:50', room: '204' },
    { course: 'CSE2001', faculty: 'Prof. Gupta', start: '13:00', end: '13:50', room: '102' },
    { course: 'MAT2002', faculty: 'Dr. Mehta', start: '14:00', end: '14:50', room: '203' },
    { course: 'PHY2003', faculty: 'Dr. Shah', start: '15:00', end: '15:50', room: 'Lab 2' },
    { course: 'ENG2004', faculty: 'Ms. Clark', start: '16:00', end: '16:50', room: '306' },
  ],
  Wednesday: [
    { course: 'ENG1004', faculty: 'Ms. James', start: '08:00', end: '08:50', room: '305' },
    { course: 'CSE1001', faculty: 'Prof. Rao', start: '09:00', end: '09:50', room: '101' },
    { course: 'CHE1005', faculty: 'Dr. Verma', start: '10:00', end: '10:50', room: '202' },
    { course: 'MAT1002', faculty: 'Dr. Singh', start: '11:00', end: '11:50', room: '201' },
    { course: 'ELE2001', faculty: 'Dr. Nair', start: '12:00', end: '12:50', room: '401' },
    { course: 'CSE2001', faculty: 'Prof. Gupta', start: '13:00', end: '13:50', room: '102' },
    { course: 'MAT2002', faculty: 'Dr. Mehta', start: '14:00', end: '14:50', room: '203' },
    { course: 'PHY2003', faculty: 'Dr. Shah', start: '15:00', end: '15:50', room: 'Lab 2' },
    { course: 'ENG2004', faculty: 'Ms. Clark', start: '16:00', end: '16:50', room: '306' },
  ],
  Thursday: [
    { course: 'PHY1003', faculty: 'Dr. Patel', start: '08:00', end: '08:50', room: 'Lab 1' },
    { course: 'ENG1004', faculty: 'Ms. James', start: '09:00', end: '09:50', room: '305' },
    { course: 'CSE1001', faculty: 'Prof. Rao', start: '10:00', end: '10:50', room: '101' },
    { course: 'CHE1005', faculty: 'Dr. Verma', start: '11:00', end: '11:50', room: '202' },
    { course: 'PHY2003', faculty: 'Dr. Shah', start: '12:00', end: '12:50', room: 'Lab 2' },
    { course: 'CSE2001', faculty: 'Prof. Gupta', start: '13:00', end: '13:50', room: '102' },
    { course: 'MAT2002', faculty: 'Dr. Mehta', start: '14:00', end: '14:50', room: '203' },
    { course: 'ENG2004', faculty: 'Ms. Clark', start: '15:00', end: '15:50', room: '306' },
    { course: 'CHE2005', faculty: 'Dr. Jain', start: '16:00', end: '16:50', room: '204' },
  ],
  Friday: [
    { course: 'MAT1002', faculty: 'Dr. Singh', start: '08:00', end: '08:50', room: '201' },
    { course: 'PHY1003', faculty: 'Dr. Patel', start: '09:00', end: '09:50', room: 'Lab 1' },
    { course: 'ENG1004', faculty: 'Ms. James', start: '10:00', end: '10:50', room: '305' },
    { course: 'CHE1005', faculty: 'Dr. Verma', start: '11:00', end: '11:50', room: '202' },
    { course: 'CSE2001', faculty: 'Prof. Gupta', start: '12:00', end: '12:50', room: '102' },
    { course: 'MAT2002', faculty: 'Dr. Mehta', start: '13:00', end: '13:50', room: '203' },
    { course: 'PHY2003', faculty: 'Dr. Shah', start: '14:00', end: '14:50', room: 'Lab 2' },
    { course: 'ENG2004', faculty: 'Ms. Clark', start: '15:00', end: '15:50', room: '306' },
    { course: 'CHE2005', faculty: 'Dr. Jain', start: '16:00', end: '16:50', room: '204' },
  ],
  Saturday: [
    { course: 'CSE1001', faculty: 'Prof. Rao', start: '09:00', end: '10:30', room: '101' },
    { course: 'LAB Project', faculty: 'Staff', start: '10:45', end: '12:15', room: 'Innovation Lab' },
    { course: 'MAT1002', faculty: 'Dr. Singh', start: '12:30', end: '13:20', room: '201' },
    { course: 'ELE2001', faculty: 'Dr. Nair', start: '13:30', end: '14:20', room: '401' },
    { course: 'CSE2001', faculty: 'Prof. Gupta', start: '14:30', end: '15:20', room: '102' },
    { course: 'MAT2002', faculty: 'Dr. Mehta', start: '15:30', end: '16:20', room: '203' },
    { course: 'PHY2003', faculty: 'Dr. Shah', start: '16:30', end: '17:20', room: 'Lab 2' },
    { course: 'ENG2004', faculty: 'Ms. Clark', start: '17:30', end: '18:20', room: '306' },
    { course: 'CHE2005', faculty: 'Dr. Jain', start: '18:30', end: '19:20', room: '204' },
  ],
};

export default WEEKLY_SCHEDULE;
