FROM node:20-alpine

# Set working directory
WORKDIR /app

# Copy package.json and package-lock.json first
COPY package*.json ./

# Install dependencies strictly with legacy-peer-deps to respect the SDK 54 downgrade
RUN npm install --legacy-peer-deps

# Copy the rest of the frontend code
COPY . .

# Expose port 8081 for Expo Web
EXPOSE 8081

# Start the Expo web server
CMD ["npm", "run", "web"]
