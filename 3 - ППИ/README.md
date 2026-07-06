
# Refrigerator Booking System

The project is a prototype web service for managing shared refrigerators in a
dormitory. The system helps track products, distribute shelves between
residents, monitor expiration dates, and reduce conflicts over shared space.

The project was completed as part of the User Interface Design course at ITMO
University. Based on the defense and final report, the work received **91 out
of 100 points (grade 5A)**.

## Features

- Adding and viewing products with owner and expiration date information
- Visual shelf occupancy display
- Notifications for upcoming expiration and overdue products
- User roles: student, dorm monitor, administrator
- Dashboard with refrigerator occupancy analytics
- Dorm monitor assignment and refrigerator management for administrators

## Technology Stack

- React
- TypeScript
- Vite
- HTML / CSS

## Local Run

Requirements:

- Node.js 18+ (20 LTS is preferred)
- npm (comes with Node)

Steps:

1. Install dependencies:

```bash
npm install
```

2. Start the dev server:

```bash
npm run dev
```

3. Open the application in a browser. By default, Vite starts at
   `http://localhost:3000` (the port is configured in `vite.config.ts`).

## Build (Optional)

```bash
npm run build
```
