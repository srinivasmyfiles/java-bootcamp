package com.coderscampus;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Application {
	static User[] users = new User[20];
	private static UserService userService = new UserService();
	static Scanner scanner = new Scanner(System.in);
	
	public static void main(String[] args) throws IOException {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader("users.txt"));

			String line = null;
			int i = 0;
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(", ");
				if ("super_user".equals(values[3])) {
					users[i++] = new SuperUser(values[0], values[1], values[2]);
				} else {
					users[i++] = new NormalUser(values[0], values[1], values[2]);
				}
			}

		} finally {
			if (reader != null)
				reader.close();
		}

		User loggedInUser = null;
		int loginAttempts = 1;
		while (loggedInUser == null && loginAttempts < 5) {
			System.out.println("Enter your email:");
			String username = scanner.nextLine();
			System.out.println("Enter your password: ");
			String password = scanner.nextLine();
			
			loggedInUser = userService.getValidUser(username, password);
			if (loggedInUser == null) {
				System.out.println("Invalid login, please try again");
				loginAttempts++;
				if (loginAttempts >= 5) {
					System.out.println("Too many failed login attempts, you are now locked out.");
				}
			}
		}
		
		if (loggedInUser != null) {
			int option = 0;
			while (option != 4) {
				System.out.println("Welcome: " + loggedInUser.getName());
				option = promptOptions(loggedInUser);
				if (option == 0 && "super_user".equals(loggedInUser.getRole())) {
					String usernameToUpdate = promptUsernameToUpdate();
					User userToUpdate = userService.getUserByUsername(usernameToUpdate);
					if (userToUpdate == null) {
						System.out.println("Invalid username.");
					} else {
						loggedInUser = userToUpdate;
					}
				} else if (option == 1) {
					promptUpdateUsername(loggedInUser);
				} else if (option == 2) {
					promptUpdatePassword(loggedInUser);
				} else if (option == 3) {
					promptUpdateName(loggedInUser);
				} else if (option != 4) {
					System.out.println("Invalid input, try again");
				}
			}
			
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter("users.txt"));
				Arrays.sort(users);
				
				for (User user : users) {
					writer.write(userService.getCsvOutput(user));
				}
			} finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
		
	}

	private static String promptUsernameToUpdate() {
		System.out.println("Which user would you like to login as? (Type in a valid username)");
		String usernameToUpdate = scanner.nextLine();
		return usernameToUpdate;
	}

	private static void promptUpdateName(User loggedInUser) {
		System.out.println("Please type in your new name: ");
		String name = scanner.nextLine();
		loggedInUser.setName(name);
	}

	private static void promptUpdatePassword(User loggedInUser) {
		System.out.println("Please type in your new password: ");
		String password = scanner.nextLine();
		loggedInUser.setPassword(password);
	}

	private static void promptUpdateUsername(User loggedInUser) {
		System.out.println("Please type in your new username: ");
		String username = scanner.nextLine();
		loggedInUser.setUsername(username);
	}

	private static int promptOptions(User loggedInUser) {
		System.out.println("----------");
		System.out.println("Please choose from the following options:");
		if (loggedInUser instanceof SuperUser) {
			System.out.println("(0) Log in as another user ");
		}
		System.out.println("(1) Update username");
		System.out.println("(2) Update password");
		System.out.println("(3) Update name");
		System.out.println("(4) Exit");
		String option = scanner.nextLine();
		
		return Integer.parseInt(option);
	}

}
