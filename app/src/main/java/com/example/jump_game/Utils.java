package com.example.jump_game;

import java.util.ArrayList;
import java.util.Random;

public final class Utils {
    public static void removeLeavedFloor(int[][] floors) {
        for (int i = 0; i < floors.length - 1; i++) {
            floors[i] = floors[i + 1];
        }
    }

    public static void generateNewFloor(int[][] floors) {
        int[] previous_floor = floors[floors.length - 2];
        ArrayList<Integer> possible_positions = new ArrayList();
        for (int i = 0; i < previous_floor.length; i++) {
            Random random;
            int left = previous_floor[i] - 1;
            int right = previous_floor[i] + 1;
            boolean is_hard = false;
            if (left < 0) {
                possible_positions.add(right);
                continue;
            } else if (right > 6) {
                possible_positions.add(left);
                continue;
            } else {
                random = new Random();
                if (random.nextInt(7) < 6) {
                    is_hard = true;
                }
            }
            if (!is_hard || random.nextBoolean()) {
                if (possible_positions.size() == 0 || left != possible_positions.get(possible_positions.size() - 1)) {
                    possible_positions.add(left);
                }
                if (!is_hard) possible_positions.add(right);
            } else {
                possible_positions.add(right);
            }
        }
        floors[floors.length - 1] = new int[possible_positions.size()];
        for (int i = 0; i < possible_positions.size(); i++) {
            floors[floors.length - 1][i] = possible_positions.get(i);
        }
    }

    public static boolean isCorrectTransition(int position, int[] correct_variants) {
        if (position < 0 || position > 6) return false;
        boolean is_correct = false;
        for (int i = 0; i < correct_variants.length; i++) {
            if (correct_variants[i] == position) {
                is_correct = true;
                break;
            }
        }
        return is_correct;
    }

    public static int randomFishPosition(int position, int[] floor) {
        ArrayList<Integer> possible_positions = new ArrayList();
        int start = (position <= 1) ? position : position - 2;
        int end = (position >= 5) ? position : position + 2;
        for (int i = start; i <= end; i += 2) {
            for (int j = 0; j < floor.length; j++) {
                if (floor[j] == i) {
                    possible_positions.add(i);
                    break;
                } else if (floor[j] > i) break;
            }
        }
        Random random = new Random();
        return possible_positions.get(random.nextInt(possible_positions.size()));
    }
}