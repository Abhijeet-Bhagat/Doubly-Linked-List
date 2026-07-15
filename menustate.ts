import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { MenuGroup, MenuItem, NavRailItem } from '../../model/menu.model';
import { MenuApiService } from './menu-api';
import { PermissionService } from '../../security/services/permission.service';
import { RoutePermissionService } from '../../security/services/route-permission.service';

@Injectable({ providedIn: 'root' })
export class MenuStateService {
  private readonly menuApiService = inject(MenuApiService);
  private readonly router = inject(Router);
  private readonly permissions = inject(PermissionService);
  private readonly routePermissions = inject(RoutePermissionService);

  readonly isOpen = signal<boolean>(false);
  readonly isLoading = signal<boolean>(false);
  readonly selectedRailItemId = signal<number | null>(null);

  /** Unfiltered menu as loaded from menu.json. */
  private readonly rawMenu = signal<NavRailItem[]>([]);

  /** Permission-filtered menu. Rebuilds automatically when permissions change. */
  readonly menuDefinitions = computed<NavRailItem[]>(() =>
    this.rawMenu()
      .map((rail: NavRailItem) => this.filterRail(rail))
      .filter((rail): rail is NavRailItem => rail !== null)
  );

  readonly selectedRailItem = computed<NavRailItem | null>(() => {
    const id: number | null = this.selectedRailItemId();
    return this.menuDefinitions().find((rail: NavRailItem) => rail.id === id) ?? null;
  });

  async toggle(): Promise<void> {
    if (this.isOpen()) {
      this.close();
      return;
    }
    await this.open();
  }

  async open(): Promise<void> {
    if (this.rawMenu().length === 0) {
      await this.loadMenu();
    }
    this.isOpen.set(true);
    this.ensureSelectedRailItem();
  }

  close(): void {
    this.isOpen.set(false);
  }

  selectRailItem(id: number): void {
    this.selectedRailItemId.set(id);
  }

  navigateTo(item: MenuItem): void {
    if (item.isExternal) {
      if (item.externalUrl) {
        window.open(item.externalUrl, '_blank', 'noopener,noreferrer');
      }
      return;
    }
    if (item.routePath) {
      this.router.navigate([item.routePath]);
      this.close();
    }
  }

  navigateToRail(rail: NavRailItem): void {
    if (rail.groups.length > 0) {
      this.selectRailItem(rail.id);
      return;
    }
    if (rail.isExternal) {
      if (rail.externalUrl) {
        window.open(rail.externalUrl, '_blank', 'noopener,noreferrer');
      }
      this.close();
      return;
    }
    if (rail.routePath) {
      this.router.navigate([rail.routePath]);
      this.close();
    }
  }

  // ---- permission filtering (bottom-up) ----

  private filterRail(rail: NavRailItem): NavRailItem | null {
    const groups: MenuGroup[] = rail.groups
      .map((group: MenuGroup) => this.filterGroup(group))
      .filter((group): group is MenuGroup => group !== null);

    // A rail survives if it still has groups, or is itself a permitted destination.
    if (groups.length === 0 && !this.canSee(rail)) {
      return null;
    }
    return { ...rail, groups };
  }

  private filterGroup(group: MenuGroup): MenuGroup | null {
    const items: MenuItem[] = group.items.filter((item: MenuItem) => this.canSee(item));
    return items.length === 0 ? null : { ...group, items };
  }

  private canSee(node: { isExternal: boolean; routePath: string | null }): boolean {
    if (node.isExternal) {
      return true;                                    // target system owns its own auth
    }
    if (!node.routePath) {
      return false;                                   // pure container, no own destination
    }
    return this.permissions.has(this.routePermissions.permissionFor(node.routePath));
  }

  // ---- loading ----

  private async loadMenu(): Promise<void> {
    if (this.rawMenu().length > 0) {
      return;
    }
    this.isLoading.set(true);
    try {
      const menu: NavRailItem[] = await firstValueFrom(this.menuApiService.getMenu());
      const sorted: NavRailItem[] = [...menu]
        .sort((a: NavRailItem, b: NavRailItem) => a.displayOrder - b.displayOrder)
        .map((rail: NavRailItem) => ({
          ...rail,
          groups: [...rail.groups]
            .sort((a: MenuGroup, b: MenuGroup) => a.displayOrder - b.displayOrder)
            .map((group: MenuGroup) => ({
              ...group,
              items: [...group.items].sort(
                (a: MenuItem, b: MenuItem) => a.displayOrder - b.displayOrder
              ),
            })),
        }));
      this.rawMenu.set(sorted);
    } finally {
      this.isLoading.set(false);
      this.ensureSelectedRailItem();
    }
  }

  private ensureSelectedRailItem(): void {
    if (this.selectedRailItemId() !== null) {
      return;
    }
    const first: NavRailItem | null =
      this.menuDefinitions().find((rail: NavRailItem) => rail.groups.length > 0) ??
      this.menuDefinitions()[0] ??
      null;
    this.selectedRailItemId.set(first?.id ?? null);
  }
}
