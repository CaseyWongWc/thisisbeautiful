# Wilderness Survival System (WSS) Game

> **IMPORTANT FOR ALL AGENTS**: Please review this README before starting work on this project. This document serves as our central reference for tracking progress, issues, and plans. After implementing changes, update the relevant sections.
>
> **File Organization:**
> - `shared/schema.ts`: Contains data models and interfaces for the entire project
> - `client/src/lib/game/`: Game logic (terrain, mapping, AI, combat, etc.)
> - `client/src/components/game/`: React UI components
> - `client/src/pages/game.tsx`: Main game page with state management

A survival game where players navigate from west to east through various terrains, managing resources, trading with NPCs, and battling enemies.

## Completed Features ✅

- [x] Core terrain-based map generation
- [x] Player resource management (strength, water, food, gold)
- [x] Various terrain types with different movement costs
- [x] Collectible resources scattered across the map
- [x] Basic AI for autonomous navigation and decision-making
- [x] Trading system with NPCs
- [x] Path finding and visualization
- [x] Enemy system with different types (wolf, bear, snake, scorpion, bandit)
- [x] Combat mechanics with health, damage, and defense stats

## Planned Features 🔄

- [ ] Balance enemy spawning and combat difficulty
- [ ] Human player control mode (alternative to AI)
- [ ] Customizable enemy spawners based on terrain
- [ ] Enhanced trading UI with more visual feedback
- [ ] Expanded equipment/upgrade system for players
- [ ] More diverse resource types and items
- [ ] Additional enemy behaviors and AI patterns
- [ ] Save/load game functionality
- [ ] Tutorial mode for new players

## Known Issues 🐛

- [ ] Enemy rendering has visual bugs when enemies are defeated
- [ ] Combat balance needs adjustment for different difficulty levels
- [ ] ID generation issues in the enemy module
- [ ] Some TypeScript errors in enemy generation code

## Content Ideas 💡

- [ ] Special boss enemies that appear at certain map locations
- [ ] Weather effects that impact movement and resource consumption
- [ ] Day/night cycle with different gameplay mechanics
- [ ] Quests/missions from traders
- [ ] Multiple character classes with different starting stats
- [ ] Procedurally generated events/encounters

## Technical Debt 🔧

- [ ] Refactor the combat system for better code organization
- [ ] Improve type safety throughout the codebase
- [ ] Optimize enemy spawning algorithm for better performance
- [ ] Add comprehensive unit tests
- [ ] Better error handling for edge cases

## Communication Guidelines

- When requesting changes, be specific about which feature or component you want to modify
- Before implementing major changes, confirm with the user first
- If encountering issues or errors, document them clearly in the "Known Issues" section
- Reference specific files when discussing code changes
- Focus on one feature/change at a time to avoid introducing multiple issues

## Project Context 

- This project is based on a previous Java implementation that was more complex
- Future goals include implementing spawners for items and NPCs
- The project is sensitive to changes - avoid making untested modifications
- When in doubt about implementation details, ask for clarification rather than making assumptions
- Reference the original Java version when appropriate for understanding complex mechanics

## User Suggestions

<!-- Add user suggestions here as they come up -->
