import { test, expect } from '@playwright/test';
import path from 'path';

test('load a map', async ({ page }) => {
  test.setTimeout(15000)
  await page.goto('localhost:8080/');

  const fileChooserPromise = page.waitForEvent('filechooser');

  await page.getByRole('button', { name: 'Import .xml delivery map file' }).click()

  const fileChooser = await fileChooserPromise;
  await fileChooser.setFiles(path.join(__dirname, 'petitPlan.xml'));

  await page.getByRole('button', { name: 'Confirm Map' }).click()
  await page.waitForTimeout(2000)

  await test.step('load a tour', async() => {
    test.setTimeout(15000)

    const fileChooserPromise = page.waitForEvent('filechooser');

    page.getByRole('button', { name: 'Import .xml delivery tour file' }).click()

    const fileChooser = await fileChooserPromise;
    await fileChooser.setFiles(path.join(__dirname, 'petitPlan.xml'));

    await page.waitForTimeout(1000)
    await page.getByRole('button', { name: 'Confirm Tour' }).click()
    await page.waitForTimeout(1000)

    await page.getByRole('button', { name: 'Show Optimal Tour' }).click()
    await page.waitForTimeout(2000)
  });
});