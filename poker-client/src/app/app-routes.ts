/**
 * Source of truth for all route paths and labels.
 */
export const APP_ROUTES = {
  LOGIN: {path: 'login', label: 'Log In'},
  LOGOUT: {path: 'logout', label: 'Log Out'},
  REGISTER: {path: 'register', label: 'Register'},
  HOME: {path: '', label: 'Home'},
  CREATE_GAME: {path: 'create', label: 'Create'},
  JOIN_GAME: {path: 'join', label: 'Join'},
  GAME_PREFIX: {path: 'game', label: 'Game'},
  GAME_ID: {path: 'gameId', label: 'Game ID'}
};

/**
 * Routes that will be open to unauthenticated users.
 */
export const OPEN_ROUTES = [
  APP_ROUTES.HOME.path,
  APP_ROUTES.LOGIN.path,
  APP_ROUTES.REGISTER.path
];
